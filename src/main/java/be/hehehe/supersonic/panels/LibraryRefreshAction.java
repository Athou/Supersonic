package be.hehehe.supersonic.panels;

import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.swing.AbstractAction;

import org.jdesktop.swingx.JXErrorPane;

import be.hehehe.supersonic.service.Library;
import be.hehehe.supersonic.utils.SupersonicException;

@SuppressWarnings("serial")
public class LibraryRefreshAction extends AbstractAction {

	@Inject
	Library library;

	@Inject
	LibraryRefreshDialog dialog;

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.setVisible(true);
		try {
			library.refresh();
		} catch (SupersonicException e1) {
			dialog.dispose();
			JXErrorPane.showDialog(e1);
		}
	}
}
