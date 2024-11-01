package edu.kh.todo.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
@RequestMapping("todo") // "/todo"로 시작하는 모든 요청 매핑
@Slf4j
public class TodoController {

	@Autowired // 같은 타입 + 상속관계 Bean을 의존성 주입(DI)
	private TodoService service;
	
	@PostMapping("add")	// "/todo/add" POST 방식 요청 매핑
	public String addTodo(
			@RequestParam("todoTitle") String todoTitle,
			@RequestParam("todoContent") String todoContent,
			RedirectAttributes ra
			) {
		
		// RedirectAttributes : 리다이렉트 시 값을 1회성으로 전달하는 객체
		
		// RedirectAttributes.addFlashAttribute("key", value) 형식으로 세팅
		// -> request scope -> session scope로 잠시 변환
		
		// 응답 전 : request scope
		// redirect 중 : session scope로 이동
		// 응답 후 : request scope 복귀
		
		// 서비스 메서드 호출 후 결과 반환 받기
		int result = service.addTodo(todoTitle, todoContent);	
		
		// 삽입 결과에 따라 message 값 지정
		String message = null;
		
		if(result > 0) message = "할 일 추가 성공!!!";
		else 			message = "할 일 추가 실패...";
		
		// 리다이렉트 후 1회성으로 사용할 데이터를 속성으로 추가
		ra.addFlashAttribute("message", message);
		
		return "redirect:/"; // 메인페이지 재요청
	}
	
	@GetMapping("detail")		// /todo/detail GET 방식 요청 매핑
	public String todoDetail(@RequestParam("todoNo") int todoNo, 
							Model model, 
							RedirectAttributes ra) {
		
		Todo todo = service.todoDetail(todoNo);
		
		String path = null;
		
		// 조회 결과가 있을 경우 /detail.html forward
		if(todo != null) {
			// templates/todo/detail.html
			
			// 접두사 : classpath:/templates/
			// 접미사 : .html
			// -> src/main/resources/templates/todo/detail.html
			path = "todo/detail";
			
			model.addAttribute("todo", todo); // request scope 값 세팅
			
		} else {
			// 조회 결과가 없을 경우 메인페이지로 리다이렉트(message : 해당 할일이 존재하지 않습니다)
			path = "redirect:/";
			
			// RedirectAttibutes : 리다이렉트 시 데이터를 session scope로 잠시 이동 시킬 수 있는 1회성 값 전달용 객체
			ra.addFlashAttribute("massage", "해당 할 일이 존재하지 않습니다");
		}
		
		return path;
	}
	
	/** 완료 여부 변경
	 * @param todo : 커맨드 객체 (@ModelAttribute 생략)
	 * 			- 파라미터의 key와 Todo 객체의 필드명이 일치하면
	 * 			- 일치하는 필드값이 세팅된 상태
	 * 			- todoNo, complete 두 필드가 세팅된 상태
	 * @return
	 */
	@GetMapping("changeComplete")
	public String changeComplete(@ModelAttribute Todo todo,
			RedirectAttributes ra) {
		
		// 변경 서비스 호출
		int result = service.changeComplete(todo);
		
		// 변경 성공 시 : "변경 성공!!"
		// 변경 실패 시 : "변경 실패.."
		String message = null;
		
		if(result > 0) message = "변경 성공!!!";
		else 			message = "변경 실패...";
		
		ra.addFlashAttribute("message", message);
		
		// 상대 경로(현재 위치)
		// 현재 주소 : /todo/changeComplete
		// 재요청 주소 : /todo/detail
		return "redirect:detail?todoNo=" + todo.getTodoNo();
	}
	
	@GetMapping("delete")
	public String delete(@RequestParam("todoNo") int todoNo,
						RedirectAttributes ra) {
		
		int result = service.deleteTodo(todoNo);
		
		String message = null;
		String path = null;
		if(result > 0) {
			message = "삭제 성공!!";
			path = "/";
		} else {
			message = "삭제 실패..";
			path = "detail?todoNo=" + todoNo;
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	@GetMapping("update")
	public String update(@RequestParam("todoNo") int todoNo
						, Model model) {
		
		// update에서 사용할 세부 정보 로드
		Todo todo = service.todoDetail(todoNo);
		
		model.addAttribute("todo", todo);
	
		
		return "todo/update";	
	}
	
	@PostMapping("update")
	public String update(@RequestParam("todoNo") int todoNo,
						@RequestParam("todoTitle") String todoTitle,
						@RequestParam("todoContent") String todoContent,
						RedirectAttributes ra) {
		
		// update 서비스 호출
		int result = service.updateTodo(todoNo, todoTitle, todoContent);
		
		String message = null;
		String path = null;

		// 수정성공시 main으로, 실패시 update 호출
		if(result > 0) {
			message = "수정 성공!!";
			path = "/";
		} else {
			message = "수정 실패!!";
			path = "update?todoNo=" + todoNo;
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;		
	}
	
}










