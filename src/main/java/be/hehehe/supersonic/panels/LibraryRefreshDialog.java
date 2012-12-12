package be.hehehe.supersonic.panels;

import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
		getContentPane().setLayout(new MigLayout("fillx"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModal(true);

		setTitle("Refreshing Library");
		setIconImage(iconService.getIcon("arrow_rotate_clockwise").getImage());

		JLabel lblRefreshingLibrary = new JLabel("Refreshing Library ...");
		getContentPane().add(lblRefreshingLibrary, "growx, wrap");

		progressBar = new JProgressBar();
		getContentPane().add(progressBar, "growx, wrap");
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("");

		JButton btnCancel = new JButton("Cancel");
		// TODO cancel refresh on click
		getContentPane().add(btnCancel, "span, right");

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
