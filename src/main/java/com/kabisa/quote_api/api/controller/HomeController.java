package com.kabisa.quote_api.api.controller;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.dto.QuoteWithRating;
import com.kabisa.quote_api.api.service.QuoteService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Getter @Setter
@AllArgsConstructor
public class HomeController {

    private final QuoteService quoteService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("random-quote")
    public String randomQuote(RedirectAttributes redirectAttributes) {
        Quote quote = quoteService.getRandomQuote();
        redirectAttributes.addFlashAttribute("quote", quote);
        return "redirect:/quote";
    }

    @GetMapping("/quote")
    public String showQuote(@ModelAttribute("quote") Quote quote, Model model) {
        if (quote.quote() == null || quote.author() == null) {
            quote = quoteService.getRandomQuote();
            model.addAttribute("quote", quote);
        }
        return "quote";
    }

    @PostMapping("/rate-quote")
    public String rateQuote(QuoteRequestDto quote, RedirectAttributes redirectAttributes) {
        quoteService.rateQuote(quote);
        redirectAttributes.addFlashAttribute("message", "Your review was submitted successfully!");

        return "redirect:/rating-result";
    }

    @GetMapping("/rating-result")
    public String showRatingSuccess(Model model) {
        List<QuoteWithRating> topRated = quoteService.getHighestRatedQuotes();
        model.addAttribute("quotes", topRated);
        return "rating-result";
    }

    @GetMapping("top-rated")
    public String showTopRatedQuotes(Model model) {
        model.addAttribute("quotes", quoteService.getHighestRatedQuotes());
        return "top-rated";
    }

}
