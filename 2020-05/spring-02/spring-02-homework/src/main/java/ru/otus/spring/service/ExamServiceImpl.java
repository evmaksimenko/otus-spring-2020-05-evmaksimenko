package ru.otus.spring.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.spring.domain.Topic;

@Service
public class ExamServiceImpl implements ExamService {
    private final TopicService topicService;
    private final TopicPresentationService topicPresentationService;
    private final Integer totalQuestionCount;
    private final Integer passQuestionCount;

    @Autowired
    public ExamServiceImpl(TopicService topicService,
                           TopicPresentationService topicPresentationService,
                           @Value("${exam.totalquestioncount:-1}") Integer totalQuestionCount,
                           @Value("${exam.passquestioncount:-1}") Integer passQuestionCount) {
        this.topicService = topicService;
        this.topicPresentationService = topicPresentationService;
        this.totalQuestionCount = totalQuestionCount;
        this.passQuestionCount = passQuestionCount;
    }

    @Override
    public double proceedExam() {
        List<Topic> topicList = topicService.getTopicList();
        Collections.shuffle(topicList);

        String name = topicPresentationService.getUserName();
        String surname = topicPresentationService.getUserSurname();
        int correct = 0;
        int topicCount = 0;
        for (Topic topic : topicList) {
            topicPresentationService.showQuestion(topic);
            topicCount++;

            if (topicPresentationService.getUserAnswer(topic)) {
                correct++;
            }
            if (totalQuestionCount > 0 && topicCount >= totalQuestionCount) {
                break;
            }
        }
        topicPresentationService.showResult(name, surname, correct, topicCount);
        boolean mark = (totalQuestionCount > 0 && passQuestionCount > 0) ? correct >= passQuestionCount :
                correct > 0.75 * topicCount;
        topicPresentationService.showMark(mark);
        return topicCount != 0 ? (1.0 * correct) / topicCount : 0;
    }
}
