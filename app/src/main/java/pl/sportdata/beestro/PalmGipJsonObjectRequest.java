package pl.sportdata.beestro;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PalmGipJsonObjectRequest extends JsonObjectRequest {

    public PalmGipJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        init();
    }

    public PalmGipJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        init();
    }

    private void init() {
        setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
