package site.hanschen.pretty.db.repository;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.db.gen.PictureDao;
import site.hanschen.pretty.db.gen.QuestionDao;

/**
 * @author HansChen
 */
public class PrettyRepositoryImpl implements PrettyRepository {

    private PictureDao  mPictureDao;
    private QuestionDao mQuestionDao;

    public PrettyRepositoryImpl(PictureDao pictureDao, QuestionDao questionDao) {
        mPictureDao = pictureDao;
        mQuestionDao = questionDao;
    }

    @Override
    public long insertOrReplace(Question question) {
        return mQuestionDao.insertOrReplace(question);
    }

    @Override
    @Nullable
    public Question getQuestion(int questionId) {
        return mQuestionDao.queryBuilder().where(QuestionDao.Properties.QuestionId.eq(questionId)).build().unique();
    }

    @Override
    public List<Question> getAllQuestion() {
        return mQuestionDao.loadAll();
    }

    @Override
    public void deleteQuestion(int questionId) {
        Question question = getQuestion(questionId);
        List<Picture> pictures = getPictures(questionId);
        mPictureDao.deleteInTx(pictures);
        mQuestionDao.delete(question);
    }

    @Override
    public long insertOrReplace(Picture picture) {
        return mPictureDao.insertOrReplace(picture);
    }

    @Override
    public List<Picture> getPictures(int questionId) {
        Question question = getQuestion(questionId);
        return question == null ? new ArrayList<Picture>() : question.getPictures();
    }

    @Override
    @Nullable
    public Picture getPicture(String url) {
        return mPictureDao.queryBuilder().where(PictureDao.Properties.Url.eq(url)).build().unique();
    }

    @Override
    public void deletePictures(int questionId) {
        List<Picture> pictures = getPictures(questionId);
        mPictureDao.deleteInTx(pictures);
    }

    @Override
    public void deletePicture(String url) {
        Picture picture = getPicture(url);
        mPictureDao.delete(picture);
    }
}
