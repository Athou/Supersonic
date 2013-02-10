package be.hehehe.supersonic.action;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.JXErrorPane;

import be.hehehe.supersonic.panels.LibraryRefreshDialog;
import be.hehehe.supersonic.service.Library;
import be.hehehe.supersonic.service.Library.RefreshMode;
import be.hehehe.supersonic.utils.SupersonicException;

@SuppressWarnings("serial")
@Singleton
public class LibraryRefreshAction extends AbstractAction {

	@Inject
	Library library;

	@Inject
	LibraryRefreshDialog dialog;

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel("Refresh Mode");
			JComboBox refreshType = new JComboBox();
			refreshType
					.setModel(new DefaultComboBoxModel(RefreshMode.values()));
			panel.add(label);
			panel.add(refreshType);

			if (CollectionUtils.isEmpty(library.getAlbums())) {
				refreshType.setSelectedItem(RefreshMode.FULL);
				refreshType.setEnabled(false);
			}
			int result = JOptionPane.showConfirmDialog(null, panel,
					"Library Refresh", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				library.refresh((RefreshMode) refreshType.getSelectedItem());
				dialog.setVisible(true);
			}
		} catch (SupersonicException e1) {
			dialog.dispose();
			JXErrorPane.showDialog(e1);
		}
	}
}
