package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.domain.CategoryRepository;
import ch.jtaf.domain.SeriesRepository;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.ui.util.LogoUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.jooq.exception.DataAccessException;

import java.io.Serial;
import java.util.List;

import static ch.jtaf.db.tables.Series.SERIES;

@Route(layout = MainLayout.class)
public class SeriesListView extends ProtectedGridView<SeriesRecord> {

    @Serial
    private static final long serialVersionUID = 1L;
    private final CategoryRepository categoryRepository;

    public SeriesListView(SeriesRepository seriesRepository, OrganizationProvider organizationProvider, CategoryRepository categoryRepository) {
        super(seriesRepository, organizationProvider, SERIES);

        setHeightFull();

        var add = new Button(getTranslation("Add"));
        add.setId("add-series");
        add.addClickListener(event -> UI.getCurrent().navigate(SeriesView.class));

        grid.setId("series-grid");

        grid.addComponentColumn(LogoUtil::resizeLogo).setHeader(getTranslation("Logo")).setAutoWidth(true);
        grid.addColumn(SeriesRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(SERIES.NAME.getName());

        grid.addColumn(seriesRecord -> categoryRepository.countAthletesBySeriesId(seriesRecord.getId()))
            .setHeader(getTranslation("Number.of.Athletes")).setAutoWidth(true);

        grid.addComponentColumn(seriesRecord -> {
            var hidden = new Checkbox();
            hidden.setReadOnly(true);
            hidden.setValue(seriesRecord.getHidden());
            return hidden;
        }).setHeader(getTranslation("Hidden")).setAutoWidth(true);
        grid.addComponentColumn(seriesRecord -> {
            var locked = new Checkbox();
            locked.setReadOnly(true);
            locked.setValue(seriesRecord.getLocked());
            return locked;
        }).setHeader(getTranslation("Locked")).setAutoWidth(true);

        grid.addComponentColumn(seriesRecord -> {
            var delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event ->
                new ConfirmDialog(
                    "delete-series-confirm-dialog",
                    getTranslation("Confirm"),
                    getTranslation("Are.you.sure"),
                    getTranslation("Delete"), e -> {
                    try {
                        seriesRepository.deleteSeries(seriesRecord.getId());
                        refreshAll();
                    } catch (DataAccessException ex) {
                        Notification.show(ex.getMessage());
                    }
                },
                    getTranslation("Cancel"), e -> {
                }).open());

            var horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add).setAutoWidth(true).setKey("delete-column");

        grid.addItemClickListener(event -> UI.getCurrent().navigate(SeriesView.class, event.getItem().getId()));

        add(grid);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Series");
    }

    @Override
    protected Condition initialCondition() {
        return SERIES.ORGANIZATION_ID.eq(organizationRecord.getId());
    }

    @Override
    protected List<OrderField<?>> initialSort() {
        return List.of(SERIES.NAME.desc());
    }
}
