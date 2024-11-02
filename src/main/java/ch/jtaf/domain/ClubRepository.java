package ch.jtaf.domain;

import ch.jtaf.db.tables.Club;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.martinelli.oss.jooqspring.JooqRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.jtaf.db.tables.Club.CLUB;

@Repository
public class ClubRepository extends JooqRepository<Club, ClubRecord, Long> {

    public ClubRepository(DSLContext dslContext) {
        super(dslContext, CLUB);
    }

    public List<ClubRecord> findByOrganizationId(Long organizationId) {
        return dslContext
            .selectFrom(CLUB)
            .where(CLUB.ORGANIZATION_ID.eq(organizationId))
            .fetch();
    }
}
