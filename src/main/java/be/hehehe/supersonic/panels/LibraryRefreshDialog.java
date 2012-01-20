package be.hehehe.supersonic.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import be.hehehe.supersonic.service.IconService;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class LibraryRefreshDialog extends JDialog {

	@Inject
	IconService iconService;

	public LibraryRefreshDialog() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][]"));
		setTitle("Refreshing Library");
		setIconImage(iconService.getIcon("arrow_rotate_clockwise").getImage());
		setModal(true);

		JLabel lblRefreshingLibrary = new JLabel("Refreshing Library ...");
		getContentPane().add(lblRefreshingLibrary, "cell 0 0");

		JProgressBar progressBar = new JProgressBar();
		getContentPane().add(progressBar, "cell 0 1,growx");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,growx");
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btnCancel = new JButton("Cancel");
		panel.add(btnCancel);

		pack();
		setPreferredSize(new Dimension(300, getHeight()));
	}
}
