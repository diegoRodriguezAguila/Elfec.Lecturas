package com.elfec.lecturas.logica_negocio.web_services;

import java.io.IOException;
import java.net.Proxy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.elfec.lecturas.modelo.TokenServicioWeb;
import com.elfec.lecturas.modelo.eventos.EventoAlObtenerResultado;
import com.elfec.lecturas.settings.VariablesDeEntorno;
import com.elfec.lecturas.settings.VariablesDeSesion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Se encarga de realizar las conexiones remotas a travez de internet, en ella estan los metodos 
 * para acceder a los servicios web
 * @author drodriguez
 *
 */
public class ManejadorConexionRemota 
{
	private static String URL;// = "http://192.168.30.44:8080/ws_lecturas.cfc?wsdl";
	private static String SOAP_ACTION; //= "";  
	private static String NAMESPACE;// = "http://DefaultNamespace";
	private static final String REGISTRO_LECTURA = "regLectura";
	private static final String REGISTRO_LECTURA_ENTRE_LINEAS = "regLecturaEnLin";
	private static final String REGISTRO_ORDENATIVO = "regOrden";
	
	static
	{
		URL = VariablesDeEntorno.protocoloServicioWeb+"://"+VariablesDeEntorno.ipServidorServicioWeb+":"+VariablesDeEntorno.puertoServicioWeb+"/ws_lecturas.cfc?wsdl";
		SOAP_ACTION = VariablesDeEntorno.accionSoap;
		NAMESPACE = VariablesDeEntorno.nombreEspacio;
	}
	
	public static boolean existeConexionAInternet(Context context) 
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return (ni != null);
	}
	
	public static void guardarLectura(Lectura lectura)
	{
		ConexionServicioWeb servicioWeb = new ConexionServicioWeb(REGISTRO_LECTURA, lectura);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		String fechaHora = df.format(lectura.FechaLecturaActual)+" "+lectura.HoraLectura;
		ParametroSoap[] params = new ParametroSoap[19];
		params[0] = new ParametroSoap("RUTA", lectura.Ruta);
		params[1] = new ParametroSoap("ANIO", lectura.Anio);
		params[2] = new ParametroSoap("MES", lectura.Mes);
		params[3] = new ParametroSoap("DIA", lectura.Dia);
		params[4] = new ParametroSoap("NUS", lectura.NusCliente);
		params[5] = new ParametroSoap("NUMMEDIDOR", lectura.NumeroMedidor);
		params[6] = new ParametroSoap("LECTURA_ACTUAL", lectura.LecturaNueva);
		params[7] = new ParametroSoap("POTENCIA_LEIDA", lectura.PotenciaLectura==null?0:(lectura.PotenciaLectura.LecturaNuevaPotencia==null?0:lectura.PotenciaLectura.LecturaNuevaPotencia.doubleValue()));
		params[8] = new ParametroSoap("LECTURA_REACTIVA", lectura.PotenciaLectura==null?0:(lectura.PotenciaLectura.Reactiva==null?0:lectura.PotenciaLectura.Reactiva.doubleValue()));
		params[9] = new ParametroSoap("IMP_TOTAL", lectura.ImporteTotal.toString());
		params[10] = new ParametroSoap("POT_FACTURADA",((lectura.PotenciaLectura==null)?"-1":lectura.PotenciaLectura.ConsumoFacturado));
		params[11] = new ParametroSoap("AUD_FECHA", fechaHora);
		params[12] = new ParametroSoap("GPSLATITUD", lectura.GPSLatitud);
		params[13] = new ParametroSoap("GPSLONGITUD", lectura.GPSLongitud);
		params[14] = new ParametroSoap("ESTADO", lectura.getEstadoLectura().getEstadoEntero());
		params[15] = new ParametroSoap("RECORDATORIO", lectura.Recordatorio==null?"":lectura.Recordatorio);
		params[16] = new ParametroSoap("usuario", VariablesDeSesion.getUsuarioLogeado());
		params[17] = new ParametroSoap("contrasena", VariablesDeSesion.getPasswordUsuario());
		params[18] = new ParametroSoap("token", TokenServicioWeb.obtenerTokenActual());
		servicioWeb.execute(params);
	}
	
	
	public static void guardarLecturaEntreLineas(MedidorEntreLineas medEntreLineas)
	{
		ConexionServicioWeb servicioWeb = new ConexionServicioWeb(REGISTRO_LECTURA_ENTRE_LINEAS, medEntreLineas);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(medEntreLineas.FechaLectura);
		int anio = calendar.get(Calendar.YEAR), mes = (calendar.get(Calendar.MONTH)+1), dia = calendar.get(Calendar.DAY_OF_MONTH);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		String fechaHora = df.format(medEntreLineas.FechaLectura)+" "+medEntreLineas.HoraLectura;
		ParametroSoap[] params = new ParametroSoap[16];
		params[0] = new ParametroSoap("RUTA",medEntreLineas.Ruta);
		params[1] = new ParametroSoap("ANIO",anio);
		params[2] = new ParametroSoap("MES", mes);
		params[3] = new ParametroSoap("DIA", dia);
		params[4] = new ParametroSoap("SUMINISTRO",-1);
		params[5] = new ParametroSoap("NUMMEDIDOR", medEntreLineas.NumeroMedidor);
		params[6] = new ParametroSoap("LECTURA_ACTUAL", medEntreLineas.LecturaNueva);
		params[7] = new ParametroSoap("POTENCIA_LEIDA", medEntreLineas.LecturaPotencia==null?0:medEntreLineas.LecturaPotencia.doubleValue());
		params[8] = new ParametroSoap("LECTURA_REACTIVA", medEntreLineas.Reactiva==null?0:medEntreLineas.Reactiva.doubleValue());
		params[9] = new ParametroSoap("AUD_FECHA", fechaHora);
		params[10] = new ParametroSoap("GPSLATITUD", medEntreLineas.GPSLatitud);
		params[11] = new ParametroSoap("GPSLONGITUD", medEntreLineas.GPSLongitud);
		params[12] = new ParametroSoap("ESTADO", 1);
		params[13] = new ParametroSoap("usuario", VariablesDeSesion.getUsuarioLogeado());
		params[14] = new ParametroSoap("contrasena", VariablesDeSesion.getPasswordUsuario());
		params[15] = new ParametroSoap("token", TokenServicioWeb.obtenerTokenActual());
		servicioWeb.execute(params);
	}
	
	public static void guardarOrdenativo(OrdenativoLectura ordLec)
	{
		ConexionServicioWeb servicioWeb = new ConexionServicioWeb(REGISTRO_ORDENATIVO, ordLec);
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		String fechaHora = df.format(ordLec.Fecha)+" "+ordLec.Hora;
		ParametroSoap[] params = new ParametroSoap[10];
		params[0] = new ParametroSoap("RUTA",ordLec.Lectura.Ruta);
		params[1] = new ParametroSoap("ANIO",ordLec.Lectura.Anio);
		params[2] = new ParametroSoap("MES", ordLec.Lectura.Mes);
		params[3] = new ParametroSoap("DIA", ordLec.Lectura.Dia);
		params[4] = new ParametroSoap("NUS", ordLec.Lectura.Suministro);
		params[5] = new ParametroSoap("ORDENATIVO", ordLec.Ordenativo.Codigo);
		params[6] = new ParametroSoap("AUD_FECHA", fechaHora);
		params[7] = new ParametroSoap("usuario", VariablesDeSesion.getUsuarioLogeado());
		params[8] = new ParametroSoap("contrasena", VariablesDeSesion.getPasswordUsuario());
		params[9] = new ParametroSoap("token", TokenServicioWeb.obtenerTokenActual());
		servicioWeb.execute(params);
	}
	
	public static class ConexionServicioWeb extends AsyncTask<ParametroSoap, Double, Double>
	{
		private String nombreMetodoSoap;
		private EventoAlObtenerResultado evento;
		public ConexionServicioWeb(String nombreMetodoSoap, EventoAlObtenerResultado evento)
		{
			this.nombreMetodoSoap = nombreMetodoSoap;
			this.evento = evento;
		}
		
		public ConexionServicioWeb(String nombreMetodoSoap)
		{
			this.nombreMetodoSoap = nombreMetodoSoap;
		}
		@Override
		protected Double doInBackground(ParametroSoap... params) {
			SoapObject request = new SoapObject(NAMESPACE, nombreMetodoSoap);
			Double resultsString=0.0;
			for (int i = 0; i < params.length; i++) 
			{
				if(params[i].Valor instanceof String || params[i].Valor instanceof Integer ||
						params[i].Valor instanceof Long || params[i].Valor instanceof Double ||
						params[i].Valor instanceof Float)
					request.addProperty(params[i].Nombre, params[i].Valor);
			}
			SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
			HttpTransportSE ht = getHttpTransportSE();
			try 
			{
				ht.call(SOAP_ACTION, envelope);
				resultsString = (Double)envelope.getResponse();
			} 
			catch (HttpResponseException e) 
			{
				Log.d(nombreMetodoSoap, e.toString());
			} 
			catch (IOException e) 
			{
				Log.d(nombreMetodoSoap, e.toString());
			} 
			catch (XmlPullParserException e) 
			{
				Log.d(nombreMetodoSoap, e.toString());
			}
			return resultsString;
		}
		
		@Override
		protected void onPostExecute(Double result)
		{
			Log.d(nombreMetodoSoap, "Resultado del webservice fue: "+result);
			if(evento!=null)
			{
				evento.procesarResultado(result);
			}
		}
		
	}
	
	private static class ParametroSoap
	{
		public String Nombre;
		public Object Valor;
		
		public ParametroSoap(String nombre, Object valor) 
		{
			Nombre = nombre;
			Valor = valor;
		}
		
	}
	
	private static SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.dotNet = false;
	    envelope.implicitTypes = true;
	    envelope.setAddAdornments(false);
	    envelope.setOutputSoapObject(request); 
	    MarshalDouble md = new MarshalDouble();
	    md.register(envelope);
	    return envelope;
	}
	
	private static HttpTransportSE getHttpTransportSE() {
	    HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,URL,60000);
	    ht.debug = true;
	    ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
	    return ht;
	}
	
	public static class MarshalDouble implements Marshal 
	{


	    @Override
		public Object readInstance(XmlPullParser parser, String namespace, String name, 
	            PropertyInfo expected) throws IOException, XmlPullParserException {
	        
	        return Double.parseDouble(parser.nextText());
	    }


	    @Override
		public void register(SoapSerializationEnvelope cm) {
	         cm.addMapping(cm.xsd, "double", Double.class, this);
	        
	    }


	    @Override
		public void writeInstance(XmlSerializer writer, Object obj) throws IOException {
	           writer.text(obj.toString());
	        }
	    
	}
}
