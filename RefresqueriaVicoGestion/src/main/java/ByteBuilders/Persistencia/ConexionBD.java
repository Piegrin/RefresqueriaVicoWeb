package ByteBuilders.Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/VicoDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Tijuana";
    private static final String DEFAULT_USUARIO = "root";
    private static final String DEFAULT_CONTRASENA = "root";

    private static String obtenerPropiedad(String claveEnv, String valorDefecto) {
        String valor = System.getenv(claveEnv);
        if (valor == null || valor.trim().isEmpty()) {
            valor = System.getProperty(claveEnv);
        }
        return (valor != null && !valor.trim().isEmpty()) ? valor.trim() : valorDefecto;
    }

    public static Connection obtenerConexion() {
        String url = obtenerPropiedad("VICO_DB_URL", DEFAULT_URL);
        String usuario = obtenerPropiedad("VICO_DB_USER", DEFAULT_USUARIO);
        String contrasena = obtenerPropiedad("VICO_DB_PASSWORD", DEFAULT_CONTRASENA);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, usuario, contrasena);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar con MySQL (" + url + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
