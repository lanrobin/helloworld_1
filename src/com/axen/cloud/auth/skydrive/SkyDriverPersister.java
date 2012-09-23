package com.axen.cloud.auth.skydrive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import com.axen.cloud.auth.IOauthPersister;
import com.axen.utils.ArrayUtil;
import com.axen.utils.Config;
import com.axen.utils.L;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveConnectSession;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Xml;

public class SkyDriverPersister implements IOauthPersister {

	private Context mContext = null;

	private static final String FILE_NAME = "skydrivepreference";
	private static final String MY_NAME = "skydrive";
	private static final String SCOPE_SEP = ",";
	private static final String TAG = "SkyDriverPersister";

	@Override
	public boolean persist(Object obj) {
		if (obj instanceof LiveConnectSession) {
			if (mContext == null) {
				return false;
			}
			return saveToXML(mContext, (LiveConnectSession) obj);
		}
		return false;
	}

	@Override
	public Object getOauthObject() {
		try {
			InputStream is = mContext.openFileInput(FILE_NAME);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance(); // 取得DocumentBuilderFactory实例
			DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
			Document doc = builder.parse(is); // 解析输入流 得到Document实例
			Element rootElement = doc.getDocumentElement();
			NodeList items = rootElement.getElementsByTagName(MY_NAME);
			L.d(TAG, "NodeList len = " + items.getLength());
			if (items.getLength() > 0) {
				// 只解析一个，也只有一个
				PropertiesHolder ph = new PropertiesHolder();
				NodeList children = items.item(0).getChildNodes();
				Node n = null;
				String name = null;
				for (int j = 0; j < children.getLength(); j++) {
					n = children.item(j);
					name = n.getNodeName();
					L.d(TAG, "name:" + name + ", value:" + n.getTextContent());
					if (ACCESS_TOKEN_KEY.equals(name)) {
						ph.atk = base64To(n.getTextContent());
					} else if (REFRESH_TOKEN_KEY.equals(name)) {
						ph.rtk = base64To(n.getTextContent());
					} else if (SCOPES.equals(name)) {
						ph.scopes = base64To(n.getTextContent());
					} else if (EXPIRED_TIME.equals(name)) {
						ph.et = base64To(n.getTextContent());
					} else if (TOKEN_TYPE.equals(name)) {
						ph.tt = base64To(n.getTextContent());
					} else if (AUTHENTICATION_TOKEN_KEY.equals(name)) {
						ph.authtk = base64To(n.getTextContent());
					} else {
						L.d(TAG,
								"Unkown node name:" + name + ", value:"
										+ n.getTextContent());
					}
				}
				LiveAuthClient client = new LiveAuthClient(mContext,
						Config.SKYDRIVE_CLIENT_ID);
				LiveConnectSession session = new LiveConnectSession(client);
				session.setAccessToken(ph.atk);
				session.setRefreshToken(ph.rtk);
				session.setAuthenticationToken(ph.authtk);
				session.setExpiresIn(new Date(ph.et));
				session.setScopes(Arrays.asList(ph.scopes.split(SCOPE_SEP)));
				session.setTokenType(ph.tt);

				return session;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SkyDriverPersister(Context c) {
		mContext = c;
	}

	private boolean saveToXML(Context c, LiveConnectSession s) {
		try {
			String str = null;
			XmlSerializer serializer = Xml.newSerializer();
			OutputStream os = c.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			Writer writer = new OutputStreamWriter(os);

			serializer.setOutput(writer);

			// xml 文件开始
			serializer.startDocument(ENCODING, true);

			serializer.startTag("", MY_NAME);
			// serializer.text(toBase64(MY_NAME));

			str = s.getAccessToken();
			if (!TextUtils.isEmpty(str)) {
				serializer.startTag("", ACCESS_TOKEN_KEY);
				serializer.text(toBase64(str));
				serializer.endTag("", ACCESS_TOKEN_KEY);
			}

			str = s.getRefreshToken();
			if (!TextUtils.isEmpty(str)) {
				serializer.startTag("", REFRESH_TOKEN_KEY);
				serializer.text(toBase64(str));
				serializer.endTag("", REFRESH_TOKEN_KEY);
			}

			serializer.startTag("", SCOPES);
			serializer.text(toBase64(ArrayUtil.iterableToString(s.getScopes(),
					SCOPE_SEP)));
			serializer.endTag("", SCOPES);

			serializer.startTag("", EXPIRED_TIME);
			serializer.text(toBase64(s.getExpiresIn().toGMTString()));
			serializer.endTag("", EXPIRED_TIME);

			str = s.getTokenType();
			if (!TextUtils.isEmpty(str)) {
				serializer.startTag("", TOKEN_TYPE);
				serializer.text(toBase64(str));
				serializer.endTag("", TOKEN_TYPE);
			}

			str = s.getAuthenticationToken();
			if (!TextUtils.isEmpty(str)) {
				serializer.startTag("", AUTHENTICATION_TOKEN_KEY);
				serializer.attribute("", OPTIONAL, "true");
				serializer.text(toBase64(str));
				serializer.endTag("", AUTHENTICATION_TOKEN_KEY);
			}

			serializer.endTag("", MY_NAME);
			serializer.endDocument();

			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private String toBase64(String s) {
		return s;
		//return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
	}

	private String base64To(String s) {
		//return new String(Base64.decode(s, Base64.DEFAULT));
		return s;
	}

	private class PropertiesHolder {
		public String atk;
		public String rtk;
		public String scopes;
		public String et;
		public String tt;
		public String authtk;
	}
}
