package jmcdw.bcnsobrerodes.Utils;

import android.content.Context;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Persistence {

    static Connection conexionMySQL;

    public Persistence() {

    }

    public boolean connectMySQL(Context context, String host, String port, String catalog, String user, String password) {
        boolean ret = true;
        if (conexionMySQL == null) {

            //String urlConexionMySQL = "jdbc:mysql://" + host + ":"
            //        + port+ "/" + catalog;
            String urlConexionMySQL = "jdbc:mysql://sql2.freemysqlhosting.net/sql294691";

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                ret = false;
                Toast.makeText(context,
                        "ClassNotFound: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            try {
                conexionMySQL = DriverManager.getConnection(urlConexionMySQL,
                        user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                ret = false;
                Toast.makeText(context,
                        "SqlException: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return ret;
    }

    public static String executeQuery(Boolean isAModification, Context context, String query) {
        try {
            String resultadoSQL = "";
            //ejecutamos consulta SQL de selección (devuelve datos)
            if (!isAModification) {
                Statement st = conexionMySQL.createStatement();
                ResultSet rs = st.executeQuery(query);

                Integer numColumnas = 0;

                //número de columnas (campos) de la consula SQL
                numColumnas = rs.getMetaData().getColumnCount();

                //obtenemos el título de las columnas
                for (int i = 1; i <= numColumnas; i++) {
                    if (resultadoSQL != "")
                        if (i < numColumnas)
                            resultadoSQL = resultadoSQL +
                                    rs.getMetaData().getColumnName(i).toString() + ";";
                        else
                            resultadoSQL = resultadoSQL +
                                    rs.getMetaData().getColumnName(i).toString();
                    else if (i < numColumnas)
                        resultadoSQL =
                                rs.getMetaData().getColumnName(i).toString() + ";";
                    else
                        resultadoSQL =
                                rs.getMetaData().getColumnName(i).toString();
                }


                //mostramos el resultado de la consulta SQL
                while (rs.next()) {
                    resultadoSQL = resultadoSQL + "n";

                    //obtenemos los datos de cada columna
                    for (int i = 1; i <= numColumnas; i++) {
                        if (rs.getObject(i) != null) {
                            if (resultadoSQL != "")
                                if (i < numColumnas)
                                    resultadoSQL = resultadoSQL +
                                            rs.getObject(i).toString() + ";";
                                else
                                    resultadoSQL = resultadoSQL +
                                            rs.getObject(i).toString();
                            else if (i < numColumnas)
                                resultadoSQL = rs.getObject(i).toString() + ";";
                            else
                                resultadoSQL = rs.getObject(i).toString();
                        } else {
                            if (resultadoSQL != "")
                                resultadoSQL = resultadoSQL + "null;";
                            else
                                resultadoSQL = "null;";
                        }
                    }
                    resultadoSQL = resultadoSQL + "n";
                }
                st.close();
                rs.close();
            }
            // consulta SQL de modificación de
            // datos (CREATE, DROP, INSERT, UPDATE)
            else {
                int numAfectados = 0;
                Statement st = conexionMySQL.createStatement();
                numAfectados = st.executeUpdate(query);
                resultadoSQL = "Registros afectados: " + String.valueOf(numAfectados);
                st.close();
            }
            return resultadoSQL;
        } catch (Exception e) {
            Toast.makeText(context,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return "";
        }
    }

}
