package be.hehehe.supersonic.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class LibraryRefreshDialog extends JDialog {

	@Inject
	IconService iconService;

	private JProgressBar progressBar;

	@PostConstruct
	public void init() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][]"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModal(true);

		setTitle("Refreshing Library");
		setIconImage(iconService.getIcon("arrow_rotate_clockwise").getImage());

		JLabel lblRefreshingLibrary = new JLabel("Refreshing Library ...");
		getContentPane().add(lblRefreshingLibrary, "cell 0 0");

		progressBar = new JProgressBar();
		getContentPane().add(progressBar, "cell 0 1,growx");
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,growx");
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btnCancel = new JButton("Cancel");
		panel.add(btnCancel);

		pack();
		setPreferredSize(new Dimension(300, getHeight()));
		SwingUtils.centerContainer(this);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			progressBar.setValue(0);
		}
		super.setVisible(b);
	}

	public void onLibraryUpdate(@Observes final LibraryChangedEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setIndeterminate(false);
				if (e.isDone()) {
					dispose();
				} else {
					progressBar.setString(e.getProgress() + "/" + e.getTotal());
					progressBar.setMinimum(0);
					progressBar.setMaximum(e.getTotal());
					progressBar.setValue(e.getProgress());
				}
			}
		});
	}
}
