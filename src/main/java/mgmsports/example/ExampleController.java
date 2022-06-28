package mgmsports.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Example controller
 *
 * @author haitran
 */
@Controller
public class ExampleController {

    @GetMapping("/sayHello")
    public String sayHello(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "sayHello";
    }

    @GetMapping("/sayGoodbye")
    public String sayGoodbye(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "sayGoodbye";
    }
}
