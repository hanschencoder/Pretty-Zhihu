package site.hanschen.pretty.db.repository;

import android.support.annotation.Nullable;

import java.util.List;

import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.bean.Question;

/**
 * @author HansChen
 */
public interface PrettyRepository {

    long insertOrReplace(Question question);

    @Nullable
    Question getQuestion(int questionId);

    List<Question> getAllQuestion();

    void deleteQuestion(int questionId);

    long insertOrReplace(Picture picture);

    List<Picture> getPictures(int questionId);

    @Nullable
    Picture getPicture(String url);

    void deletePictures(int questionId);

    void deletePicture(String url);
}
