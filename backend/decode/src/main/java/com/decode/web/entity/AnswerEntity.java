package com.decode.web.entity;

import com.decode.web.global.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "Answer")
@NoArgsConstructor
public class AnswerEntity extends CommonEntity {

    @Id
    @Column(name = "answer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "answer_writer_id")
    private UserInfoEntity answerWriter;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    @Setter
    @Column(name = "answer_content")
    private String content;

    @Setter
    @Column(name = "is_adopted")
    private boolean idAdopted;

    public AnswerEntity(Long answerId, UserInfoEntity answerWriter, QuestionEntity question,
            String content, boolean idAdopted) {
        this.answerId = answerId;
        this.answerWriter = answerWriter;
        this.question = question;
        this.content = content;
        this.idAdopted = idAdopted;
    }
}
