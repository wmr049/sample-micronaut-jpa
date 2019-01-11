package sample.context.orm;

import java.time.LocalDateTime;

import javax.inject.Singleton;

import sample.context.Timestamper;
import sample.context.actor.*;

/**
 * Entityの永続化タイミングでAOP処理を差し込む Interceptor。
 */
@Singleton
public class OrmInterceptor {

    private final ActorSession session;
    private final Timestamper time;
    
    public OrmInterceptor(ActorSession session, Timestamper time) {
        this.session = session;
        this.time = time;
    }

    /** 登録時の事前差し込み処理を行います。  */
    public void touchForCreate(Object entity) {
        if (entity instanceof OrmActiveMetaRecord) {
            Actor staff = session.actor();
            LocalDateTime now = time.date();
            OrmActiveMetaRecord<?> metaEntity = (OrmActiveMetaRecord<?>) entity;
            metaEntity.setCreateId(staff.getId());
            metaEntity.setCreateDate(now);
            metaEntity.setUpdateId(staff.getId());
            metaEntity.setUpdateDate(now);
        }
    }

    /** 変更時の事前差し込み処理を行います。   */
    public boolean touchForUpdate(final Object entity) {
        if (entity instanceof OrmActiveMetaRecord) {
            Actor staff = session.actor();
            LocalDateTime now = time.date();
            OrmActiveMetaRecord<?> metaEntity = (OrmActiveMetaRecord<?>) entity;
            if (metaEntity.getCreateDate() == null) {
                metaEntity.setCreateId(staff.getId());
                metaEntity.setCreateDate(now);
            }
            metaEntity.setUpdateId(staff.getId());
            metaEntity.setUpdateDate(now);
        }
        return false;
    }

}
