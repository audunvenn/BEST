package compliancevalidator.ui;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * @author audunvennesland
 * 3. okt. 2017 
 */
public class DownloadFile {

	/*	class SaveL implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	      JFileChooser c = new JFileChooser();
	      // Demonstrate "Save" dialog:
	      int rVal = c.showSaveDialog(FileChooserTest.this);
	      if (rVal == JFileChooser.APPROVE_OPTION) {
	        filename.setText(c.getSelectedFile().getName());
	        dir.setText(c.getCurrentDirectory().toString());
	      }
	      if (rVal == JFileChooser.CANCEL_OPTION) {
	        filename.setText("You pressed cancel");
	        dir.setText("");
	      }
	    }
	  }*/

	JFileChooser fileChooser = new JFileChooser();

	StringBuilder sb1 = new StringBuilder();

	public File storeFile() throws Exception {

		File file = null;
		File dir = null;

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			//get the directory
			dir = fileChooser.getCurrentDirectory();
			file = fileChooser.getSelectedFile();
			

			sb1.append(file.getName());

		} else {


			sb1.append("No file selected!");
		}

		return file;

	}


}
