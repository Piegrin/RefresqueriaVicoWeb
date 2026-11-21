package ByteBuilders.Persistencia;

import ByteBuilders.Entidad.*;
import java.sql.*;
import java.util.*;

public class UsuarioDAO {

    private Usuario mapearUsuario(ResultSet rs, RolDAO rolDAO) throws SQLException {
        Rol rol = null;
        int rolID = rs.getInt("RolID");
        if (rolID > 0) {
            rol = rolDAO.obtenerPorID(rolID);
        }
        return new Usuario(
                rs.getInt("UsuarioID"),
                rs.getString("Nombre"),
                rs.getString("Correo"),
                rs.getString("Telefono"),
                rs.getString("ContrasenaHash"),
                rol,
                rs.getTimestamp("FechaCreacion")
        );
    }

    public Usuario obtenerPorID(int id) {
        String sql = "SELECT * FROM Usuarios WHERE UsuarioID = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn == null) {
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapearUsuario(rs, new RolDAO());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuarios";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn == null) {
                return lista;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                RolDAO rolDAO = new RolDAO();
                while (rs.next()) {
                    lista.add(mapearUsuario(rs, rolDAO));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (Nombre, Correo, Telefono, ContrasenaHash, RolID, FechaCreacion) VALUES (?, ?, ?, ?, ?, NOW())";
        int rolID = (usuario.getRol() != null && usuario.getRol().getRolID() > 0)
                ? usuario.getRol().getRolID()
                : 3;

        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usuario.getNombre());
                stmt.setString(2, usuario.getCorreo());
                stmt.setString(3, usuario.getTelefono());
                stmt.setString(4, usuario.getContrasenaHash());
                stmt.setInt(5, rolID);

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Usuario usuario) {
        boolean actualizarContrasena = usuario.getContrasenaHash() != null
                && !usuario.getContrasenaHash().trim().isEmpty();

        int rolID;
        if (usuario.getRol() != null && usuario.getRol().getRolID() > 0) {
            rolID = usuario.getRol().getRolID();
        } else {
            Usuario actual = obtenerPorID(usuario.getIdUsuario());
            rolID = (actual != null && actual.getRol() != null) ? actual.getRol().getRolID() : 3;
        }

        String sql = actualizarContrasena
                ? "UPDATE Usuarios SET Nombre = ?, Correo = ?, Telefono = ?, ContrasenaHash = ?, RolID = ? WHERE UsuarioID = ?"
                : "UPDATE Usuarios SET Nombre = ?, Correo = ?, Telefono = ?, RolID = ? WHERE UsuarioID = ?";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usuario.getNombre());
                stmt.setString(2, usuario.getCorreo());
                stmt.setString(3, usuario.getTelefono());
                if (actualizarContrasena) {
                    stmt.setString(4, usuario.getContrasenaHash().trim());
                    stmt.setInt(5, rolID);
                    stmt.setInt(6, usuario.getIdUsuario());
                } else {
                    stmt.setInt(4, rolID);
                    stmt.setInt(5, usuario.getIdUsuario());
                }
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Usuarios WHERE UsuarioID = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario obtenerPorCredenciales(String identificador, String contrasena) {
        String sql = "SELECT * FROM Usuarios WHERE (Correo = ? OR Telefono = ?) AND ContrasenaHash = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn == null) {
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String valor = identificador.trim();
                stmt.setString(1, valor);
                stmt.setString(2, valor);
                stmt.setString(3, contrasena);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapearUsuario(rs, new RolDAO());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
