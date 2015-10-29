package jmcdw.bcnsobrerodes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import com.google.android.gms.maps.model.TileProvider;

public class WMSFactory {

    private static final String GEOSERVER_FORMAT =
            "http://www.opengis.net/wms" +
                    "?service=WMS" +
                    "&version=1.3.0" +
                    "&request=GetMap" +
                    "&layers=WMS Mobiliari" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=256" +
                    "&height=256" +
                    "&srs=EPSG:900913" +
                    "&format=image/png" +
                    "&transparent=true";

    // return a geoserver wms tile layer
    private static TileProvider getTileProvider() {
        TileProvider tileProvider = new WMSUtils(256,256) {

            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, GEOSERVER_FORMAT, bbox[MINX],
                        bbox[MINY], bbox[MAXX], bbox[MAXY]);
                URL url = null;
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