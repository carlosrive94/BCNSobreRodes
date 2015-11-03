package jmcdw.bcnsobrerodes;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import com.google.android.gms.maps.model.TileProvider;

public class WMSFactory {

    private static final String GEOSERVER_FORMAT =
            "http://w24.bcn.cat/WMSMobAltres/request.aspx" +
                    "?service=WMS" +
                    "&version=1.3.0" +
                    "&request=GetMap" +
                    "&layers=MOBILIARITOT" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=20" +
                    "&height=15" +
                    "&crs=EPSG:32631"+
                    "&format=image/png" +
                    "&transparent=true";

    // return a geoserver wms tile layer
    public static TileProvider getTileProvider() {
        TileProvider tileProvider = new WMSUtils(20, 15) {

            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, GEOSERVER_FORMAT, bbox[MINX],
                        bbox[MINY], bbox[MAXX], bbox[MAXY]);
                URL url = null;
                Log.v("WMSDEMO", s);

                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
        return tileProvider;
    }

}