/**
 * Jan Stefkovic, 11743161
 *
 * https://howtodoinjava.com/library/json-simple-read-write-json-examples/
 *
 */

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import scala.Int;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TestRunner {

    private JSONArray readList = new JSONArray();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static JSONObject findInArrayByID (JSONArray array, long key) {
        for (int i = 0; i < 1000; i++) {
            JSONObject o = (JSONObject) array.get(i);
            if ((long) o.get("id") == key) {
                return o;
            }
        }
        return null;
    }

    /** Our test file is small so we can store it all in memory. */
    @Before
    public void readAndStoreJsonFile() {

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("MOCK_DATA.json"))
        {
            Object obj = jsonParser.parse(reader);
            readList = (JSONArray) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testIfJsonFileRead() {

        // create object manually
        JSONObject fifthObject = new JSONObject();
        fifthObject.put("id",5);
        fifthObject.put("email","ltynnan4@1und1.de");

        assertEquals(findInArrayByID(readList,5).toJSONString(), fifthObject.toJSONString());

    }

    @Test
    public void storeOne() throws UnsupportedEncodingException {
        HttpPost post = new HttpPost("http://localhost:6789/save");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("key", "0"));
        urlParameters.add(new BasicNameValuePair("value", "test@mail.com"));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            String answer = EntityUtils.toString(response.getEntity());
            assertEquals("{\"message\":\"Saved Key Value\",\"success\":true}", answer);
            System.out.println(answer);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getOne() {
        HttpGet request = new HttpGet("http://localhost:6789/0");

     try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            //Header headers = entity.getContentType();
            //System.out.println(headers);

            String result = "";

            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);
                System.out.println(result);
            }

            assertEquals("{\"key\":\"0\",\"value\":\"test@mail.com\",\"success\":true}", result);

        } catch (ClientProtocolException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }
    }

    @Test
    public void storeAll() throws UnsupportedEncodingException {
        for (int i = 0; i < 1000; i++) {
            JSONObject o = (JSONObject) readList.get(i);

            String key = Long.toString((long) o.get("id"));
            String value = (String) o.get("email");

            HttpPost post = new HttpPost("http://localhost:6789/save");

            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("key", key));
            urlParameters.add(new BasicNameValuePair("value", value));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {

                String answer = EntityUtils.toString(response.getEntity());
                assertEquals("{\"message\":\"Saved Key Value\",\"success\":true}", answer);
                System.out.println(answer);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO this does not guarantee three different buckets
    @Test
    public void getThreeRandom() {

        // create instance of Random class
        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int rand_int1 = rand.nextInt(1000);
        int rand_int2 = rand.nextInt(1000);
        int rand_int3 = rand.nextInt(1000);

        // Shift it to range 1 to 1000
        rand_int1++;
        rand_int2++;
        rand_int3++;

        List<Integer> myInts = new ArrayList<Integer>();
        myInts.add(rand_int1);
        myInts.add(rand_int2);
        myInts.add(rand_int3);

        for (Integer i : myInts) {

            // Get object from data file

            // in our file the object is on index i-1
            JSONObject o = (JSONObject) readList.get(i-1);
            String key = Long.toString((long) o.get("id"));
            String value = (String) o.get("email");

            // Get object from database
            HttpGet request = new HttpGet("http://localhost:6789/" + Integer.toString(i));

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                System.out.println(response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();
                //Header headers = entity.getContentType();
                //System.out.println(headers);

                String result = "";

                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    System.out.println(result);
                }

                assertEquals("{\"key\":\"" + key + "\",\"value\":\"" + value + "\",\"success\":true}", result);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

}
