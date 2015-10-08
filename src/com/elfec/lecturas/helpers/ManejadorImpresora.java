package com.elfec.lecturas.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Looper;

import com.elfec.lecturas.modelo.excepciones.ImpresoraPredefinidaNoAsignadaException;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

/**
 * Maneja la conexion con las impresoras bluetooth, asi como la seleccion de la
 * impresora predeterminada e impresión
 * 
 * @author drodriguez
 *
 */
@SuppressWarnings("deprecation")
public class ManejadorImpresora {
	/**
	 * Constante para las impresoras zebras RW 420, para otro tipo de impresora
	 * es necesario ver otras constantes
	 */
	public static final int ZEBRA_BLUETOOTH_PRINTER = 1664;
	private static BluetoothDevice impresoraPredefinida = null;

	/**
	 * Recibe un texto en lenguaje CPCL para ser enviado a imprimir en el
	 * dispositivo seleccionado por defecto
	 * 
	 * @param textoCPCL
	 *            , texto en lenguaje CPCL
	 * @throws ImpresoraPredefinidaNoAsignadaException
	 *             , cuando no se asignó la impresoraPredefinida y se llama al
	 *             metodo
	 */
	public static void imprimir(String textoCPCL)
			throws ImpresoraPredefinidaNoAsignadaException {
		if (impresoraPredefinida == null) {
			throw new ImpresoraPredefinidaNoAsignadaException();
		}
		enviarAImprimirPorBluetooth(textoCPCL);
	}

	/**
	 * Asigna la impresora predefinida
	 * 
	 * @param macAddress
	 */
	public static void asignarImpresoraPredefinida(
			BluetoothDevice impresoraPredef) {
		impresoraPredefinida = impresoraPredef;
	}

	/**
	 * Obtiene la impresora predefinida
	 * 
	 * @return BluetoothDevice, la impresora predefinida
	 */
	public static BluetoothDevice obtenerImpresoraPredefinida() {
		return impresoraPredefinida;
	}

	/**
	 * Retorna si la impresora predefinida fue asignada
	 * 
	 * @return <b>true</b> si la impresora predefinida fue asignada,
	 *         <b>false</b> en caso de que no se haya asignado
	 */
	public static boolean impresoraPredefinidaFueAsignada() {
		return impresoraPredefinida != null;
	}

	/**
	 * Obtiene la lista de los dispositivos sincronizados con el celular
	 * 
	 * @param activity
	 *            , se utiliza para poder llamar al dialogo de activación de
	 *            bluetooth, en caso de encontrarse apagado
	 * @return lista de dispositivos bluetooth sincronizadas con el celular
	 */
	public static List<BluetoothDevice> obtenerDispositivosSincronizados(
			Context context) {
		List<BluetoothDevice> listaDispositivos = new ArrayList<BluetoothDevice>();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		for (BluetoothDevice device : pairedDevices) {
			if (device.getBluetoothClass().getDeviceClass() == ZEBRA_BLUETOOTH_PRINTER)
				listaDispositivos.add(device);
		}
		return listaDispositivos;
	}

	/**
	 * Realiza la conexión con la impresora predefinida y envia el texto, que
	 * tiene que ser en formato CPCL, a imprimir
	 * 
	 * @param textoAImprimir
	 */
	private static void enviarAImprimirPorBluetooth(final String textoAImprimir) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Instantiate insecure connection for given Bluetooth MAC
					// Address.
					// Connection conexion = new
					// BluetoothConnectionInsecure(impresoraPredefinida.getAddress());
					BluetoothPrinterConnection conexion = new BluetoothPrinterConnection(
							impresoraPredefinida.getAddress());
					// Verify the printer is ready to print
					if (impresoraEstaLista(conexion
							.getConvertedNewStyleConnection())) {
						// Initialize
						Looper.prepare();
						// Open the connection - physical connection is
						// established here.
						conexion.open();
						// Send the data to printer as a byte array.
						conexion.write((textoAImprimir).getBytes());
						// Make sure the data got to the printer before closing
						// the connection
						Thread.sleep(500);
						// Close the insecure connection to release resources.
						conexion.close();
						Looper.myLooper().quit();
					}
				} catch (Exception e) {
					// Handle communications error here.
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Verifica el estado de la impresora conectada
	 * 
	 * @param connection
	 * @return
	 */
	private static boolean impresoraEstaLista(Connection conexion) {
		boolean estaLista = false;
		try {
			conexion.open();
			// Creates a ZebraPrinter object to use Zebra specific functionality
			// like getCurrentStatus()
			ZebraPrinter impresora = ZebraPrinterFactory.getInstance(conexion);
			PrinterStatus estadoImpresora = impresora.getCurrentStatus();
			if (estadoImpresora.isReadyToPrint) {
				estaLista = true;
			} else if (estadoImpresora.isPaused) {
				System.out
						.println("Cannot Print because the printer is paused.");
			} else if (estadoImpresora.isHeadOpen) {
				System.out
						.println("Cannot Print because the printer media door is open.");
			} else if (estadoImpresora.isPaperOut) {
				System.out.println("Cannot Print because the paper is out.");
			} else {
				System.out.println("Cannot Print.");
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (ZebraPrinterLanguageUnknownException e) {
			e.printStackTrace();
		}
		return estaLista;
	}

}
