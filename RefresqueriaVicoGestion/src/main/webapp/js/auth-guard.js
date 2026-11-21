/**
 * Control de sesion y autorizacion por rol para las vistas de Refresqueria Vico.
 */
(function () {
    window.verificarAcceso = function (rolesPermitidos) {
        const usuarioID = sessionStorage.getItem('usuarioID');
        const rol = sessionStorage.getItem('rol');
        const nombre = sessionStorage.getItem('nombre');

        if (!usuarioID || !rol) {
            window.location.replace('Login.html');
            return false;
        }

        if (Array.isArray(rolesPermitidos) && rolesPermitidos.length > 0 && !rolesPermitidos.includes(rol)) {
            if (rol === 'Empleado') {
                window.location.replace('MenuVentas.html');
            } else {
                window.location.replace('home.html');
            }
            return false;
        }

        const adminNameEl = document.getElementById('adminName');
        if (adminNameEl && nombre) {
            adminNameEl.textContent = nombre.split(' ')[0];
        }
        return true;
    };

    window.cerrarSesionCompleta = function () {
        sessionStorage.clear();
        window.location.href = 'Login.html';
    };
})();
