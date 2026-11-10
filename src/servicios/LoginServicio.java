package servicios;

import dao.LoginDAO;
import dao.LoginDAO.Cuenta;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Ingreso a la aplicacion. Las claves nunca se guardan en texto: se guarda un hash PBKDF2
 * (HMAC-SHA256, 65536 iteraciones) con una sal distinta por usuario.
 */
public class LoginServicio {

    public static final String PRIMER_INGRESO = "primer_ingreso";
    private static final int ITERACIONES = 65536;
    private static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_MS = 60_000;

    private final LoginDAO dao = new LoginDAO();
    private final Map<String, int[]> fallos = new HashMap<>();       // login -> {intentos}
    private final Map<String, Long> bloqueados = new HashMap<>();    // login -> hasta cuando

    /** Resultado de un ingreso: error (o null) y la cuenta si fue correcto. */
    public static class Resultado {
        public String error;
        public Cuenta cuenta;
    }

    public synchronized Resultado ingresar(String login, String clave) {
        Resultado r = new Resultado();
        login = limpiar(login);
        Long hasta = bloqueados.get(login);
        if (hasta != null && hasta > System.currentTimeMillis()) {
            r.error = "Demasiados intentos. Espere un minuto e intente de nuevo.";
            return r;
        }
        Cuenta c = dao.buscarPorLogin(login);
        if (c == null) {
            r.error = "Usuario o contrasena incorrectos.";
            return r;
        }
        if (c.hash == null) {
            r.error = PRIMER_INGRESO;
            r.cuenta = c;
            return r;
        }
        if (clave == null || !coincide(clave, c.sal, c.hash)) {
            int[] n = fallos.computeIfAbsent(login, k -> new int[1]);
            if (++n[0] >= MAX_INTENTOS) {
                bloqueados.put(login, System.currentTimeMillis() + BLOQUEO_MS);
                n[0] = 0;
            }
            r.error = "Usuario o contrasena incorrectos.";
            return r;
        }
        fallos.remove(login);
        r.cuenta = c;
        return r;
    }

    /** Crea la clave del primer ingreso. Devuelve el error, o null si quedo guardada. */
    public synchronized String crearClave(String login, String clave, String confirmacion) {
        login = limpiar(login);
        if (clave == null || clave.length() < 6) {
            return "La contrasena debe tener al menos 6 caracteres.";
        }
        if (!clave.equals(confirmacion)) {
            return "Las contrasenas no coinciden.";
        }
        Cuenta c = dao.buscarPorLogin(login);
        if (c == null) {
            return "Usuario o contrasena incorrectos.";
        }
        if (c.hash != null) {
            return "Este usuario ya tiene contrasena.";
        }
        try {
            byte[] sal = new byte[16];
            new SecureRandom().nextBytes(sal);
            String salTexto = Base64.getEncoder().encodeToString(sal);
            String hash = calcular(clave, salTexto);
            return dao.crearClave(login, hash, salTexto) ? null : "No se pudo guardar la contrasena.";
        } catch (Exception e) {
            return "No se pudo guardar la contrasena.";
        }
    }

    private boolean coincide(String clave, String sal, String hashGuardado) {
        try {
            return MessageDigest.isEqual(calcular(clave, sal).getBytes("UTF-8"), hashGuardado.getBytes("UTF-8"));
        } catch (Exception e) {
            return false;
        }
    }

    private String calcular(String clave, String salTexto) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), Base64.getDecoder().decode(salTexto), ITERACIONES, 256);
        byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    private String limpiar(String login) {
        return login == null ? "" : login.trim().toLowerCase();
    }
}
