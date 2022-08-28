package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import petmily.service.place.PlaceService;
import petmily.service.post.PostService;
import petmily.service.walk.WalkService;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostService postService;
    private final WalkService walkService;
    private final PlaceService placeService;

    @GetMapping("/")
    public String index(Model model){

        model.addAttribute("posts", postService.findAllDesc());
        model.addAttribute("walks", walkService.findAllDesc());
        model.addAttribute("places", placeService.findAllDesc());

        return "index";
    }

}
