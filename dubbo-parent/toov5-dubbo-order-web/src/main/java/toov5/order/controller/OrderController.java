package toov5.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.toov.api.member.service.MemberService;

@RestController
public class OrderController {
    @Reference  //dubbo提供的  而不是@Autowired
	private MemberService memberService;
	@RequestMapping("/orde")
	public String orderToMember() {
		return memberService.getUser();
		
	}
	
}
