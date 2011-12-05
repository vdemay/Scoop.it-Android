package com.odeval.scoopit.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class NetworkingUtils {

    /**
     * Convert an input stream to a String
     * 
     * @param is input stream
     * @return string returned
     */
    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * Send a request to the web Returns the body of the response Returns null
     * is it is impossible for any reason to get a response
     * 
     * @param url url to request
     * @return body of the response
     */
    public static String sendRestfullRequest(String url) {
        String toReturn = null;

        HttpClient httpclient = new DefaultHttpClient();
        // Prepare a request object : GET type
        HttpGet httpget = new HttpGet(url);

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                toReturn = convertStreamToString(instream);
                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
        return toReturn;
    }

    public static String sendRestfullRequest(String url, OAuthConsumer consumer) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);

            consumer.sign(request);
            
            HttpResponse response = httpclient.execute(request);
            InputStream data = response.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
            String responeLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((responeLine = bufferedReader.readLine()) != null) {
                responseBuilder.append(responeLine);
            }
            return responseBuilder.toString();
        } catch (OAuthMessageSignerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static String sendRestfullPostRequest(String url, OAuthConsumer consumer, Map<String, String> params) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);

            List<NameValuePair> httpParams = new ArrayList<NameValuePair>();

            for(String param : params.keySet()) {
                httpParams.add(new BasicNameValuePair(param, params.get(param)));
            }
            
            //Log.d("Params:", httpParams.toString());
            try {
            	request.setEntity(new UrlEncodedFormEntity(httpParams, "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            
            
            consumer.sign(request);
            
            HttpResponse response = httpclient.execute(request);
            InputStream data = response.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
            String responeLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((responeLine = bufferedReader.readLine()) != null) {
                responseBuilder.append(responeLine);
            }
            return responseBuilder.toString();
        } catch (OAuthMessageSignerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

}