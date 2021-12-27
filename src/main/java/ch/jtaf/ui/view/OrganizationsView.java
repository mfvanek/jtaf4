package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.dialog.OrganizationDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.ui.security.OrganizationProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serial;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;
import static ch.jtaf.db.tables.OrganizationUser.ORGANIZATION_USER;

@Route(layout = MainLayout.class)
public class OrganizationsView extends VerticalLayout implements HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient DSLContext dsl;
    private final Grid<OrganizationRecord> grid;

    public OrganizationsView(DSLContext dsl, OrganizationProvider organizationProvider) {
        this.dsl = dsl;

        setHeightFull();

        OrganizationDialog dialog = new OrganizationDialog(getTranslation("Organization"));

        Button add = new Button(getTranslation("Add"));
        add.addClickListener(event -> dialog.open(ORGANIZATION.newRecord(), this::loadData));

        grid = new Grid<>();
        grid.getClassNames().add("rounded-corners");
        grid.setHeightFull();

        grid.addColumn(OrganizationRecord::getOrganizationKey).setHeader(getTranslation("Key")).setSortable(true);
        grid.addColumn(OrganizationRecord::getName).setHeader(getTranslation("Name")).setSortable(true);

        grid.addComponentColumn(organizationRecord -> {
            Button select = new Button(getTranslation("Select"));
            select.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            select.addClickListener(event -> {
                organizationProvider.setOrganization(organizationRecord);
                UI.getCurrent().navigate(SeriesListView.class);
            });

            Button delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(getTranslation("Confirm"),
                    getTranslation("Are.you.sure"),
                    getTranslation("Delete"), e -> {
                    try {
                        dsl.attach(organizationRecord);
                        organizationRecord.delete();
                    } catch (DataAccessException ex) {
                        Notification.show(ex.getMessage());
                    }
                },
                    getTranslation("Cancel"), e -> {
                });
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.open();
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(select, delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add);

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
            .ifPresent(organization -> dialog.open(organization, this::loadData)));

        loadData();

        add(grid);
    }

    private void loadData() {
        var organizations = dsl
            .select(ORGANIZATION_USER.organization().fields())
            .from(ORGANIZATION_USER)
            .where(ORGANIZATION_USER.securityUser().EMAIL.eq(SecurityContextHolder.getContext().getAuthentication().getName()))
            .fetch().into(ORGANIZATION);

        grid.setItems(organizations);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Organizations");
    }
}
