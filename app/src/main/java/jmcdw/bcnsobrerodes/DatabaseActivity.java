package jmcdw.bcnsobrerodes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView info;
    private Button button, conecta;
    String ipServidorMySQL = "sql2.freemysqlhosting.net";
    String puertoMySQL = "3306";
    String catalogoMySQL = "sql294691";
    String usuarioMySQL = "sql294691";
    String contrasenaMySQL = "uX2%mW5!";
    static String SQLEjecutar = "select * from LocalitzacioMobilitat";
    static Connection conexionMySQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_database);

        info = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        conecta = (Button) findViewById(R.id.conecta);
        conecta.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.button:
                String resultadoSQL = ejecutarConsultaSQL(false, getApplication());
                info.setText(resultadoSQL);
                break;
            case R.id.conecta:
                boolean isConnected = conectarMySQL();
                if (isConnected) {
                    Toast.makeText(this,
                            "Connected!",
                            Toast.LENGTH_SHORT).show();
                    conecta.setEnabled(false);
                }
                break;
        }
    }


    public static String ejecutarConsultaSQL(Boolean SQLModificacion, Context context) {
        try {
            String resultadoSQL = "";
            //ejecutamos consulta SQL de selección (devuelve datos)
            if (!SQLModificacion) {
                Statement st = conexionMySQL.createStatement();
                ResultSet rs = st.executeQuery(SQLEjecutar);

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
                numAfectados = st.executeUpdate(SQLEjecutar);
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


    private boolean conectarMySQL() {
        boolean ret = true;
        if (conexionMySQL == null) {
            //String urlConexionMySQL = "jdbc:mysql://" + ipServidorMySQL + ":"
            //        + puertoMySQL+ "/" + catalogoMySQL;

            String urlConexionMySQL = "jdbc:mysql://sql2.freemysqlhosting.net/sql294691";

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                ret = false;
                Toast.makeText(this,
                        "ClassNotFound: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            try {
                conexionMySQL = DriverManager.getConnection(urlConexionMySQL,
                        usuarioMySQL, contrasenaMySQL);
            } catch (SQLException e) {
                e.printStackTrace();
                ret = false;
                Toast.makeText(this,
                        "SqlException: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return ret;
    }
}


