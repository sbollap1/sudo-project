package foo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.Receiver;

public class UploadReceiver implements Receiver, FinishedListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FileOutputStream fos = null;
	private FileInputStream fis = null;
	private InputStreamReader isr = null;
	private Integer[][] matrix;

	private String fileName;

	// The receiveUpload() method is called when the user clicks the submit
	// button.
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {

		fileName = filename;

		// System.out.println("File Name inside receiveUpload = " + fileName +
		// "MIME =" + mimeType);

		try {
			fos = new FileOutputStream("sudoinput.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				fos.write(b);
			}

		};
	}

	@Override
	public void uploadFinished(FinishedEvent event) {

		fileName = event.getFilename();
		// System.out.println("File name inside upload finished = " + fileName +
		// ":: " + event.getFilename());
		// TODO Auto-generated method stub
		matrix = new Integer[9][9];

		try {
			fis = new FileInputStream("sudoinput.txt");
			isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String sCurrentLine;

			int i = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println("sCurrentLine: " + sCurrentLine);
				String[] row = sCurrentLine.split(" ");
				int j = 0;
				for (String s : row) {
					// System.out.println("s=" + Integer.parseInt(s));
					matrix[i][j] = Integer.parseInt(s);
					// System.out.println("matrix[" + i + "][" + j + "] = " +
					// matrix[i][j]);

					j++;
				}
				i++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String getFileName() {
		System.out.println("File Name inside getFileName  = " + fileName);
		return fileName;
	}

	public Integer[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(Integer[][] matrix) {
		this.matrix = matrix;
	}

}
