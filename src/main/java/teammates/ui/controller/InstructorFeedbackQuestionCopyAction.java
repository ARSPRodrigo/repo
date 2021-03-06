package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionCopyAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);
        
        new GateKeeper().verifyAccessible(
                instructorDetailForCourse, 
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        String instructorEmail = instructorDetailForCourse.email;
        
        try {
            int index = 0;
            String feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + index);
            statusToAdmin = ""; 
            
            while(feedbackQuestionId != null){
                FeedbackQuestionAttributes feedbackQuestion = logic.copyFeedbackQuestion(feedbackQuestionId, feedbackSessionName, courseId, instructorEmail);    
                index++;
                feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + index);
                statusToAdmin += "Created Feedback Question for Feedback Session:<span class=\"bold\">(" +
                        feedbackQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
                        feedbackQuestion.courseId + "]</span> created.<br>" +
                        "<span class=\"bold\">" + feedbackQuestion.getQuestionDetails().getQuestionTypeDisplayName() + 
                        ":</span> " + feedbackQuestion.getQuestionDetails().questionText;  
            }
            
            if(index > 0){
                statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
            } else {
                statusToUser.add("No questions are indicated to be copied");
                isError = true;
            }
        } catch (InvalidParametersException e) {
            statusToUser.add(e.getMessage());
            statusToAdmin = e.getMessage();
            isError = true;
        }

        return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(courseId,feedbackSessionName));
    }

}
