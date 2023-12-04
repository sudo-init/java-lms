package nextstep.qna.domain;

import nextstep.qna.CannotDeleteException;
import nextstep.users.domain.NsUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Question {

    private static final String DELETE_PERMISSION_ERROR_MESSAGE = "질문을 삭제할 권한이 없습니다.";
    private static final String DELETE_ERROR_MESSAGE = "다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.";

    private Long id;

    private String title;

    private String contents;

    private NsUser writer;

    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate;

    public Question() {
    }

    public Question(NsUser writer, String title, String contents) {
        this(0L, writer, title, contents);
    }

    public Question(Long id, NsUser writer, String title, String contents) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public NsUser getWriter() {
        return writer;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(NsUser loginUser) {
        return writer.equals(loginUser);
    }

    public Question setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public List<DeleteHistory> deleteQuestion(NsUser loginUser) throws CannotDeleteException {
        checkOwner(isOwner(loginUser), DELETE_PERMISSION_ERROR_MESSAGE);

        List<Answer> answers = getAnswers();
        for (Answer answer : answers) {
            checkOwner(answer.isOwner(loginUser), DELETE_ERROR_MESSAGE);
        }

        List<DeleteHistory> deleteHistories = new ArrayList<>();
        setDeleted(true);
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, id, writer));
        for (Answer answer : answers) {
            answer.setDeleted(true);
            deleteHistories.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter()));
        }
        return deleteHistories;
    }

    private void checkOwner(boolean deleteEnabled, String message) throws CannotDeleteException {
        if (!deleteEnabled) {
            throw new CannotDeleteException(message);
        }
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
