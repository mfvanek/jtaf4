package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
import static org.assertj.core.api.Assertions.assertThat;

class ClubsViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();

        UI.getCurrent().navigate(ClubsView.class);
    }

    @Test
    void add_club() {
        Grid<ClubRecord> clubsGrid = _get(Grid.class, spec -> spec.withId("clubs-grid"));
        assertThat(GridKt._size(clubsGrid)).isEqualTo(4);

        assertThat(GridKt._get(clubsGrid, 0).getName()).isEqualTo("Erlach");

        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(Dialog.class, 1);

        // Test maximize and restore
        Button toggle = _get(Button.class, spec -> spec.withId("toggle"));
        toggle.click();
        toggle.click();

        _get(TextField.class, spec -> spec.withCaption("Abbreviation")).setValue("Test");
        _get(TextField.class, spec -> spec.withCaption("Name")).setValue("Test");
        _get(Button.class, spec -> spec.withCaption("Save")).click();

        assertThat(GridKt._size(clubsGrid)).isEqualTo(5);

        assertThat(GridKt._get(clubsGrid, 0).getName()).isEqualTo("Test");

        GridKt._getCellComponent(clubsGrid, 0, "edit-column").getChildren()
            .filter(component -> component instanceof Button).findFirst().map(component -> (Button) component)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();

        _fireConfirm(confirmDialog);

        assertThat(GridKt._size(clubsGrid)).isEqualTo(4);
    }

}
