package site.hanschen.pretty.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @author HansChen
 */
@Entity
public class Picture {

    @Id
    private Long id;

    private int questionId;

    @NotNull
    @Unique
    private String url;

    @Generated(hash = 1760715477)
    public Picture(Long id, int questionId, @NotNull String url) {
        this.id = id;
        this.questionId = questionId;
        this.url = url;
    }

    @Generated(hash = 1602548376)
    public Picture() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
