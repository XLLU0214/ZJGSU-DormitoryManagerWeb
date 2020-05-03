package com.xllu0214.programmer.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.xllu0214.programmer.bean.Operator;
import com.xllu0214.programmer.bean.Page;
import com.xllu0214.programmer.bean.SearchProperty;
import com.xllu0214.programmer.dao.StudentDao;
import com.xllu0214.programmer.entity.Student;
import com.xllu0214.programmer.util.StringUtil;

/**
 * ѧ������
 * @author XLLU
 *
 */
public class StudentServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2810703183766136697L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String method = req.getParameter("method");
		if("toStudentListView".equals(method)){
			req.getRequestDispatcher("view/studentList.jsp").forward(req, resp);
		}
		if("AddStudent".equals(method)){
			addStudent(req,resp);
		}
		if("StudentList".equals(method)){
			getStudentList(req,resp);
		}
		if("EditStudent".equals(method)){
			editStudent(req,resp);
		}
		if("DeleteStudent".equals(method)){
			deleteStudent(req,resp);
		}
	}
	
	private void deleteStudent(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String[] ids = req.getParameterValues("ids[]");
		StudentDao studentDao = new StudentDao();
		String msg = "";
		if(studentDao.delete(ids)){
			msg = "success";
		}
		studentDao.closeConnection();
		try {
			resp.getWriter().write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void editStudent(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		Integer id = StringUtil.isEmpty(req.getParameter("id")) ? 0 : Integer.parseInt(req.getParameter("id"));
		String sn = req.getParameter("sn");
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		String sex = req.getParameter("sex");
		Student student = new Student();
		student.setId(id);
		student.setName(name);
		student.setSex(sex);
		student.setPassword(password);
		student.setSn(sn);
		StudentDao studentDao = new StudentDao();
		String msg = "";
		if(studentDao.update(student)){
			msg = "success";
		}
		studentDao.closeConnection();
		try {
			resp.getWriter().write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getStudentList(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String from = req.getParameter("from");
		//��������������ѯ
		if("combox".equals(from)){
			returnByCombox(req,resp);
			return;
		}
		int pageNumber = Integer.parseInt(req.getParameter("page"));
		int pageSize = Integer.parseInt(req.getParameter("rows"));
		String name = req.getParameter("name");
		if(StringUtil.isEmpty(name)){
			name = "";
		}
		Student student = new Student();
		Map<String, Object> ret = new HashMap<String, Object>();
		student.setName(name);
		StudentDao studentDao = new StudentDao();
		Page<Student> page = new Page<Student>(pageNumber, pageSize);
		page.getSearchProperties().add(new SearchProperty("name", "%" + name + "%", Operator.LIKE));
		//�жϵ�ǰ�û��Ƿ�Ϊѧ��
		int userType = Integer.parseInt(req.getSession().getAttribute("userType").toString());
		if(userType == 2){
			//�����ѧ������ֻ�ܲ鿴�Լ��ĸ�����Ϣ
			Student loginStudent = (Student)req.getSession().getAttribute("user");
			page.getSearchProperties().add(new SearchProperty("id", loginStudent.getId(), Operator.EQ));
		}
		Page<Student> findList = studentDao.findList(page);
		ret.put("rows",findList.getConten());
		ret.put("total",findList.getTotal());
		studentDao.closeConnection();
		resp.setCharacterEncoding("utf-8");
		try {
			resp.getWriter().write(JSONObject.fromObject(ret).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void returnByCombox(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		StudentDao studentDao = new StudentDao();
		Page<Student> page = new Page<Student>(1, 9999);
		//�жϵ�ǰ�û��Ƿ���ѧ��
		int userType = Integer.parseInt(req.getSession().getAttribute("userType").toString());
		if(userType == 2){
			//�����ѧ������ֻ�ܲ鿴���Լ�����Ϣ
			Student loginedStudent = (Student)req.getSession().getAttribute("user");
			page.getSearchProperties().add(new SearchProperty("id", loginedStudent.getId(), Operator.EQ));
		}
		page = studentDao.findList(page);
		studentDao.closeConnection();
		resp.setCharacterEncoding("utf-8");
		try {
			resp.getWriter().write(JSONArray.fromObject(page.getConten()).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		String sex = req.getParameter("sex");
		resp.setCharacterEncoding("utf-8");
		if(StringUtil.isEmpty(name)){
			resp.getWriter().write("��������Ϊ�գ�");
			return;
		}
		if(StringUtil.isEmpty(password)){
			resp.getWriter().write("���벻��Ϊ�գ�");
			return;
		}
		if(StringUtil.isEmpty(sex)){
			resp.getWriter().write("�Ա���Ϊ�գ�");
			return;
		}
		Student student = new Student();
		student.setName(name);
		student.setPassword(password);
		student.setSex(sex);
		student.setSn(StringUtil.generateSn("ZJGSU-S", ""));
		StudentDao studentDao = new StudentDao();
		String msg = "����ʧ�ܣ�";
		if(studentDao.add(student)){
			msg = "success";
		}
		studentDao.closeConnection();
		resp.getWriter().write(msg);
	}
}