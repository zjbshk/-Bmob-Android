package wendangxia.com.wdx.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import wendangxia.com.wdx.R;
import wendangxia.com.wdx.bean.Bdwd;
import wendangxia.com.wdx.bean.MyXy;


/**
 * Created by Administrator on 2018/12/14.
 */

public class Util {


    public static void getRightWDURL(String url, final Handler handler) throws UnsupportedEncodingException {

        OkHttpClient client = new OkHttpClient();
        client.setFollowSslRedirects(false);
        final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String content = "txtUrl=" + URLEncoder.encode(url, "utf-8");
        RequestBody body = RequestBody.create(mediaType, content);
        String ngwd = "http://139.196.35.122:52888/";
        Request request = new Request.Builder().url(ngwd).post(body).build();
        final Call call = client.newCall(request);
        final MyXy myXy = new MyXy();
        final Message msg = new Message();
        msg.obj = myXy;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                myXy.setStatus(R.integer.fail);
                myXy.setMsg("请检查网络是否连接");
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (response.isRedirect()) {
                    String location = response.header("Location");
                    myXy.setStatus(R.integer.success);
                    myXy.setMsg(location);
                } else {
                    myXy.setStatus(R.integer.fail);
                    myXy.setMsg("本IP用完了次数");
                }
                response.body().close();
                handler.sendMessage(msg);
            }

        };
        call.enqueue(callback);
    }

    public static void gotoDownload(Context context, String location) {
        Uri uri = Uri.parse(location);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static <T> T getKeyValue(Context context, String name, String key, Object value) {
        if (key != null) {
            List<String> keys = Arrays.asList(key);
            List<Object> values = Arrays.asList(value);
            List<Object> objects = getKeyValue(context, name, keys, values);
            if (objects.isEmpty()) {
                return null;
            } else {
                Object o = objects.get(0);
                return (T) o;
            }
        } else {
            throw new IllegalArgumentException("参数异常");
        }
    }

    public static List<Object> getKeyValue(Context context, String name, List<String> keys, List<Object> values) {
        SharedPreferences shared = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = values.get(i);
            Object o = null;
            if (value instanceof String) {
                o = shared.getString(key, (String) value);
            } else if (value instanceof Integer) {
                o = shared.getInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                o = shared.getBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                o = shared.getLong(key, (Long) value);
            } else if (value instanceof Float) {
                o = shared.getFloat(key, (Float) value);
            } else if (value instanceof Set) {
                o = shared.getStringSet(key, (Set<String>) value);
            }
            if (o != null)
                objects.add(o);
        }
        return objects;
    }

    public static void saveKeyValue(Context context, String name, String key, Object value) {
        if (key != null && value != null) {
            List<String> keys = Arrays.asList(key);
            List<Object> values = Arrays.asList(value);
            saveKeyValue(context, name, keys, values);
        } else {
            throw new IllegalArgumentException("参数不能为空");
        }
    }

    public static void saveKeyValue(Context context, String name, List<String> keys, List<Object> values) {
        SharedPreferences shared = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editer = shared.edit();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = values.get(i);
            if (value instanceof String) {
                editer.putString(key, (String) value);
            } else if (value instanceof Integer) {
                editer.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editer.putBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                editer.putLong(key, (Long) value);
            } else if (value instanceof Float) {
                editer.putFloat(key, (Float) value);
            } else if (value instanceof Set) {
                editer.putStringSet(key, (Set<String>) value);
            }
        }
        editer.apply();
    }

    public static String MD5(String sourceStr) {
        if (sourceStr == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }

    public static void gotoLoginActivity(Activity activity, Class c) {
        Intent intent = new Intent(activity, c);
        activity.startActivity(intent);

    }

    public static void downloadAppGuide(String url, final String fileName, final Context context) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
//                ToastUtil.showError(context, "error:"+e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream is = response.body().byteStream();
                File root = context.getFilesDir();
                File file = new File(root, fileName);
                OutputStream outputStream = new FileOutputStream(file);
                int len;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                }
                outputStream.flush();
                outputStream.close();
                is.close();
                call.cancel();
            }
        });
    }

    public static boolean isUrl(String url) {
        try {
            URL isUrl = new URL(url);
            String protocol = isUrl.getProtocol();
            if (!"http".equals(protocol) || !"https".equals(protocol)) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String getSubContent(String sb, String startStr, String endStr) {
        int start = sb.indexOf(startStr) + startStr.length();
        int end = sb.indexOf(endStr, start);
        return getSubContent(sb, start, end);
    }

    public static String getSubContent(String sb, int start, int end) {
        if (start == -1 || end == -1) {
            return null;
        }
        return sb.substring(start, end);
    }

    public static Bdwd getDocInfo(String sub) {
        String title = getSubContent(sub, "'title': '", "',");
        String type = getSubContent(sub, "'docType': '", "',");
        String decription = getSubContent(sub, "'docDesc': '", "',");
        String id = getSubContent(sub, "'docId': '", "',");
        String creater = getSubContent(sub, "'creater': '", "',");
        String docticket = getSubContent(sub, "'docTicket':+'", "',");
        String prize = getSubContent(sub, "'payPrice': +'", "',");

        Bdwd bdwd = new Bdwd();
        bdwd.setId(id);
        bdwd.setDescription(decription);
        bdwd.setTitle(title);
        if (creater != null && creater.contains("%")) {
            try {
                String createrTemp = URLDecoder.decode(creater, "GB2312");
                creater = createrTemp;
            } catch (UnsupportedEncodingException e) {
                System.out.println("编码异常");
            }
        }
        bdwd.setCreater(creater);
        if (docticket != null && docticket.matches("\\d+")) {
        }
        if (prize != null && prize.matches("\\d+")) {
            bdwd.setPrize(Integer.parseInt(prize) / 100);
        }
        return bdwd;
    }

    public static String getBaiduDocId(String urlpath) throws MalformedURLException {
        URL url = new URL(urlpath);
        String path = url.getPath();
        String[] paths = path.split("[/?\\.#]");
        for (String s : paths) {
            if (s.matches("[a-z0-9]{10,}")) {
                return s;
            }
        }
        return null;
    }

    public static String getDDProductId(String urlpath, String key) throws MalformedURLException {
        URL url = new URL(urlpath);
        String query = url.getQuery();
        if (query != null) {
            String[] keyvalue = query.split("&");
            for (String s : keyvalue) {
                if (s.startsWith(key + "=")) {
                    return s.replace(key + "=", "");
                }
            }
        } else {
            String path = url.getPath();
            String[] paths = path.split("[./#?]");
            for (String s : paths) {
                if (s.matches("\\d{5,}")) {
                    return s;
                }
            }
        }
        return null;
    }

    public static void showLocationShare(Context context, String title, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);//注意：这里只是分享文本内容
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, title));
    }


    public static String getFormatTime() {
        return getFormatTime("yyyy-MM-dd hh:mm:ss");
    }

    public static String getFormatTime(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String date = simpleDateFormat.format(new Date());
        return date;
    }


    public static String conver(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            String temp = "";
            int strInt = str.charAt(i);
            if (strInt > 127) {
                temp += "\\u" + Integer.toHexString(strInt);
            } else {
                temp = String.valueOf(str.charAt(i));
            }
            result += temp;
        }
        return result;
    }
}
