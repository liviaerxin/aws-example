package org.livia.aws.util;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;

public class Signature {
	
	private static final DateTimeFormatter fmt_iso8601 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);
	
	private static final DateTimeFormatter fmt_date = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);
	
	static String bytesToHex(byte[] hash) {
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	    String hex = Integer.toHexString(0xff & hash[i]);
	    if(hex.length() == 1) hexString.append('0');
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	static byte[] SHA256(String data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return digest.digest(data.getBytes(StandardCharsets.UTF_8));
	}
	
	static byte[] HmacSHA256(String data, byte[] key) throws Exception {
	    String algorithm="HmacSHA256";
	    Mac mac = Mac.getInstance(algorithm);
	    mac.init(new SecretKeySpec(key, algorithm));
	    return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	}

	static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
	    byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
	    byte[] kDate = HmacSHA256(dateStamp, kSecret);
	    byte[] kRegion = HmacSHA256(regionName, kDate);
	    byte[] kService = HmacSHA256(serviceName, kRegion);
	    byte[] kSigning = HmacSHA256("aws4_request", kService);
	    return kSigning;
	}
	
	public static String UriEncode(CharSequence input, boolean encodeSlash) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '~' || ch == '.') {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                result.append(String.format("%04x", (int) ch));
            }
        }
        return result.toString();
    }
	
	public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        System.out.println( "------------" );
		String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
		String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
		if (accessKey == null) {
			System.out.println("accessKey can't be null");
			System.exit(1);
		}
		if (secretKey == null) {
			System.out.println("secretKey can't be null");
			System.exit(1);
		}
		System.out.println("accessKey is:" + accessKey);
		System.out.println("secretKey is:" + secretKey);
        System.out.println( "------------" );
        
        String url = "https://s3.ap-northeast-2.amazonaws.com/astri-kingmed/3dhistech/test.txt";
        String httpMethod = "GET";
        String region = "ap-northeast-2";
        String service = "s3";
        String requestBody = "";
        String hashedPayload = bytesToHex(SHA256(requestBody));
        HttpRequest request = new BasicHttpRequest(httpMethod, url);
        URL website = new URL(url);
        String host = website.getHost();
        Instant now = Instant.now();
        String amzDate = fmt_iso8601.format(now);
        String date = fmt_date.format(now);
        
        request.addHeader("host", host);
        request.addHeader("x-amz-content-sha256", hashedPayload);
        request.addHeader("x-amz-date", amzDate);
        
        //1. Create a Canonical Request
        String canonicalRequest;
        System.out.println("httpMethod:" + httpMethod);
        
        String canonicalURI = website.getPath() == null ? "" : website.getPath();
        System.out.println("canonicalURI:" + canonicalURI);
        
        String canonicalQueryString = website.getQuery() == null ? "" : website.getQuery();
        System.out.println("canonicalQueryString:" + canonicalQueryString);
        
        String canonicalHeaders = "";
        String signedHeaders = "";
        for (Header h : request.getAllHeaders()) {
        	canonicalHeaders = canonicalHeaders + h.getName() + ":" + h.getValue() + "\n";
        	signedHeaders = signedHeaders + h.getName() + ";";
        }
        System.out.println("canonicalHeaders:" + canonicalHeaders);
        
        signedHeaders = signedHeaders.substring(0, signedHeaders.length() - 1);
        System.out.println("signedHeaders:" + signedHeaders);
        
        System.out.println("hashedPayload:" + hashedPayload);
        System.out.println("------------------------------");
        canonicalRequest = httpMethod + "\n" + canonicalURI + "\n" + canonicalQueryString + "\n" 
        		+ canonicalHeaders + "\n" + signedHeaders + "\n" + hashedPayload;
        System.out.println("canonicalRequest: \n" + canonicalRequest);
        
        System.out.println("------------------------------------------------");
        
        //2. Create a String to Sign
        String algorithm = "AWS4-HMAC-SHA256";
        String scope = date + "/" + region + "/" + service + "/" + "aws4_request";
        String stringToSign = algorithm + "\n" + amzDate + "\n" + scope + "\n" + bytesToHex(SHA256(canonicalRequest));
        System.out.println("stringToSign: \n" + stringToSign);
        
        System.out.println("------------------------------------------------");
        
        //3. Create Signing Key
        byte[] signingKey = getSignatureKey(secretKey, date, region, service);
        System.out.println("signingKey: \n" + bytesToHex(signingKey));
        System.out.println("------------------------------------------------");
        
        //4. Calculate Signature
        String signature = bytesToHex(HmacSHA256(stringToSign, signingKey));
        System.out.println("signature: \n" + signature);
        System.out.println("------------------------------------------------");
        
        //5. Create Authorization Header
        String authorizationHeader = algorithm + " " + "Credential=" + accessKey + "/" + scope + ", " 
        		+  "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;
        System.out.println("authorizationHeader: \n" + authorizationHeader);
        System.out.println("------------------------------------------------");
//        		method = 'GET'
//        		service = 's3'
//        		host = 's3.ap-northeast-2.amazonaws.com'
//        		region = 'ap-northeast-2'
//        		endpoint = 'https://s3.ap-northeast-2.amazonaws.com'
//        		rangex = 'bytes=0-9999999999'
    }
}
