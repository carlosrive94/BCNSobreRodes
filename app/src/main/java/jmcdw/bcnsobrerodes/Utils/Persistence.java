package jmcdw.bcnsobrerodes.Utils;

import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Persistence extends AsyncTask<String, Void, String> {

    private Connection con;
    private Context context;
    private String host = "sql2.freemysqlhosting.net";
    //private String port = "3306";
    private String catalog = "sql294691";
    private String user = "sql294691";
    private String password = "uX2%mW5!";

    public Persistence(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String query = strings[0];
        String result = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (con == null)
                con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + catalog
                        , user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int nColumns = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= nColumns; ++i)
                    //result += rsmd.getColumnName(i) + ": " + rs.getString(i) + "\n";
                    result += rs.getString(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        //return result;
    }
}