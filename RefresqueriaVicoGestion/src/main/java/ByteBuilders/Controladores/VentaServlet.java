package ByteBuilders.Controladores;

import ByteBuilders.Negocio.VentaService;
import ByteBuilders.Entidad.Venta;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/ventas/*")
public class VentaServlet extends HttpServlet {
    private final VentaService ventaService = new VentaService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarCORS(response);
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                List<Venta> ventas = ventaService.obtenerTodas();
                response.setStatus(HttpServletResponse.SC_OK);
                enviarJSON(response, gson.toJson(ventas));
            } else {
                int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
                Venta venta = ventaService.obtenerVentaPorID(id);
                if (venta == null) {
                    manejarError(response, HttpServletResponse.SC_NOT_FOUND, "Venta no encontrada: " + id);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                enviarJSON(response, gson.toJson(venta));
            }
        } catch (NumberFormatException e) {
            manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID de venta invalido");
        } catch (Exception e) {
            manejarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al consultar ventas: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarCORS(response);
        try {
            Venta venta = gson.fromJson(request.getReader(), Venta.class);
            if (venta == null || venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "La venta debe incluir al menos un producto");
                return;
            }
            boolean resultado = ventaService.realizarVenta(venta);
            if (resultado) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                JsonObject json = new JsonObject();
                json.addProperty("exito", true);
                json.addProperty("ventaID", venta.getVentaID());
                json.addProperty("mensaje", "Venta realizada correctamente");
                enviarJSON(response, gson.toJson(json));
            } else {
                manejarError(response, HttpServletResponse.SC_CONFLICT, "No se pudo completar la venta (stock insuficiente o error de registro)");
            }
        } catch (Exception e) {
            manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Error al registrar la venta: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void configurarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void enviarJSON(HttpServletResponse response, String json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    private void manejarError(HttpServletResponse response, int codigo, String mensaje) throws IOException {
        response.setStatus(codigo);
        JsonObject json = new JsonObject();
        json.addProperty("exito", false);
        json.addProperty("mensaje", mensaje);
        enviarJSON(response, gson.toJson(json));
        System.err.println("ERROR VentaServlet [" + codigo + "]: " + mensaje);
    }
}
