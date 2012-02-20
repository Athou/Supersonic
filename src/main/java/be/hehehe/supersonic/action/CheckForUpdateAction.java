package be.hehehe.supersonic.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXErrorPane;

import be.hehehe.supersonic.service.UpdateService;

@Named
public class CheckForUpdateAction implements ActionListener {

	@Inject
	UpdateService updateService;

	private boolean noUpdatesReporting;

	@Override
	public void actionPerformed(ActionEvent e) {
		new SwingWorker<Object, Void>() {
			private boolean update;

			@Override
			protected Object doInBackground() throws Exception {
				update = updateService.checkForUpdate();
				return null;
			}

			protected void done() {
				if (update) {
					int result = JOptionPane
							.showConfirmDialog(
									null,
									"An update is available, do you want to visit the download page?",
									"Update available",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						try {
							Desktop.getDesktop().browse(
									updateService.getDownloadPageURI());
						} catch (IOException e) {
							JXErrorPane.showDialog(e);
						}
					}
				} else if (noUpdatesReporting) {
					JOptionPane.showMessageDialog(null, "No updates available",
							"Updates", JOptionPane.INFORMATION_MESSAGE);
				}
			};
		}.execute();
	}

	public void setNoUpdatesReporting(boolean noUpdatesReporting) {
		this.noUpdatesReporting = noUpdatesReporting;
	}

}
