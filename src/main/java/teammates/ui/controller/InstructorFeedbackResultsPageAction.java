package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorFeedbackResultsPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		if(courseId==null || feedbackSessionName == null) {
			return createRedirectResult(Common.PAGE_INSTRUCTOR_HOME);
		}
		
		new GateKeeper().verifyCourseInstructorOrAbove(courseId);

		InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
		data.instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
		data.bundle = logic.getFeedbackSessionResultsForUser(feedbackSessionName, courseId, data.instructor.email);
		data.sortType = getRequestParam(Common.PARAM_FEEDBACK_RESULTS_SORTTYPE);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		
		if (data.sortType == null) {
			// default: sort by recipients
			return createShowPageResult(Common.JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
		}
		if (data.sortType.equals("table")){
			statusToUser.add("Displaying feedback session results as a table.");
			return createShowPageResult(Common.JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_TABLE, data);
		} else if (data.sortType.equals("recipient")) {
			statusToUser.add("Sorting results by feedback recipient's name in paragraph format.");
			return createShowPageResult(Common.JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
		} else if (data.sortType.equals("giver")) {
			statusToUser.add("Sorting results by feedback giver's name in paragraph format.");
			return createShowPageResult(Common.JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER, data);
		} else {
			// default: sort by recipients
			return createShowPageResult(Common.JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
		}
	}

}