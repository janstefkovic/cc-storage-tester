/**
 * Jan Stefkovic, 11743161
 *
 * https://howtodoinjava.com/library/json-simple-read-write-json-examples/
 * https://mkyong.com/java/how-to-send-http-request-getpost-in-java/
 * https://www.journaldev.com/878/java-write-to-file
 */

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRunner {

    /** array to store MOCK_DATA.json */
    private JSONArray readList = new JSONArray();
    /** httpClient */
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    /** simple text  log file */
    private static File file = new File("TestLog.txt");

    /** helper function to write logs with one command */
    private static void writeToLogFile(String data) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        String dataWithNewLine = data + System.getProperty("line.separator");
        try {
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);

            bw.write(dataWithNewLine);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** helper function to look for JSON (key, value) pair */
    private static JSONObject findInArrayByID (JSONArray array, long key) {
        for (int i = 0; i < 1000; i++) {
            JSONObject o = (JSONObject) array.get(i);
            if ((long) o.get("id") == key) {
                return o;
            }
        }
        return null;
    }

    /** Our test file is small so we can store it all in memory before. */
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

        writeToLogFile("Test 1: If JSON file was read correctly.");

        // create object manually
        JSONObject fifthObject = new JSONObject();
        fifthObject.put("id",5);
        fifthObject.put("email","ltynnan4@1und1.de");

        writeToLogFile("Expected: " + fifthObject.toJSONString());
        writeToLogFile("Actual: " + findInArrayByID(readList,5).toJSONString());

        assertEquals(fifthObject.toJSONString(), findInArrayByID(readList,5).toJSONString());
    }

    @Test
    public void storeOne() throws UnsupportedEncodingException {

        writeToLogFile("Test 2: Store one (key, value) pair.");

        HttpPost post = new HttpPost("http://localhost:6789/save");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("key", "0"));
        urlParameters.add(new BasicNameValuePair("value", "test@mail.com"));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            String answer = EntityUtils.toString(response.getEntity());
            assertEquals("{\"message\":\"Saved Key Value\",\"success\":true}", answer);
            writeToLogFile(answer);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getOne() {

        writeToLogFile("Test 3: Get the previously stored one (key, value) pair.");

        HttpGet request = new HttpGet("http://localhost:6789/0");

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            //writeToLogFile(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();

            String result = "";

            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);
                writeToLogFile(result);
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

        writeToLogFile("Test 4: Store all (key, value) pairs.");

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
                writeToLogFile(Integer.toString(i+1) + answer);
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

        writeToLogFile("Test 5: Access three random (key, value) pairs and compare them to our test file.");

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
            JSONObject o = findInArrayByID(readList, i);
            String key = Long.toString((long) o.get("id"));
            String value = (String) o.get("email");

            // Get object from database
            HttpGet request = new HttpGet("http://localhost:6789/" + Integer.toString(i));

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                //System.out.println(response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();

                String result = "";

                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    writeToLogFile(Integer.toString(i) + result);
                }

                assertEquals("{\"key\":\"" + key + "\",\"value\":\"" + value + "\",\"success\":true}", result);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void deleteTwoRandom() {

        writeToLogFile("Test 6: Choose two random keys, verify their presence, then delete them and verify their absence.");

        // create instance of Random class
        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int rand_int1 = rand.nextInt(1000);
        int rand_int2 = rand.nextInt(1000);

        // Shift it to range 1 to 1000
        rand_int1++;
        rand_int2++;

        List<Integer> myInts = new ArrayList<Integer>();
        myInts.add(rand_int1);
        myInts.add(rand_int2);

        for (Integer i : myInts) {

            // Get object from data file
            JSONObject o = findInArrayByID(readList, i);
            String key = Long.toString((long) o.get("id"));
            String value = (String) o.get("email");

            // Get object from database
            HttpGet request = new HttpGet("http://localhost:6789/" + Integer.toString(i));

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                //System.out.println(response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();

                String result = "";

                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    writeToLogFile(Integer.toString(i) + result);
                }

                assertEquals("{\"key\":\"" + key + "\",\"value\":\"" + value + "\",\"success\":true}", result);

                writeToLogFile("The object " + Integer.toString(i) +" is present.");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /** Delete object from database */
            HttpDelete delRequest = new HttpDelete("http://localhost:6789/" + Integer.toString(i));

            try (CloseableHttpResponse response = httpClient.execute(delRequest)) {

                // Get HttpResponse Status
                //System.out.println(response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();

                String result = "";

                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    writeToLogFile(Integer.toString(i) + result);
                }

                assertEquals("{\"message\":\"Successfully deleted\",\"success\":true}", result);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /** Check that object is not present in database */
            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                //System.out.println(response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();

                String result = "";

                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    writeToLogFile(Integer.toString(i) + result);
                }

                assertEquals("{\"success\":false,\"error\":\"NO_SUCH_KEY\"}", result);

                writeToLogFile("The object " + Integer.toString(i) +" is now absent.");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void getRange() {

        writeToLogFile("Test 7: Request a range of keys (\"2..3\") and their values.");

        HttpGet request = new HttpGet("http://localhost:6789/range/2/3");

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            //System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();

            String result = "";

            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);
                writeToLogFile(result);
            }

            /** verify if keys in result have first char '2' or '3' */
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonResult = new JSONArray();

            try
            {
                Object obj = jsonParser.parse(result);
                jsonResult = (JSONArray) obj;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < jsonResult.size(); i++) {
                JSONObject o = (JSONObject) jsonResult.get(i);
                String key = (String) o.get("key");
                String firstChar = String.valueOf(key.charAt(0));


                assertTrue("2".equals(firstChar) || "3".equals(firstChar));
                }
            } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
