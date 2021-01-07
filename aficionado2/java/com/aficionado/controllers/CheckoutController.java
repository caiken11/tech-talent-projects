package com.aficionado.controllers;

import com.aficionado.models.ChargeRequest;
import com.aficionado.models.ChargeRequest.Currency;
import com.aficionado.sevice.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CheckoutController {

   private static final String stripePublicKey = "pk_test_51Hy6yGIvrquu0viBAja8uPXRz7gKKr0LTh4fV13wpyzuitFxN91kB4M9hGV8YbTzVFMVEUxqP5UdL5zVAEBJ2WEf00yg9tPNIv";

   @GetMapping("/checkout")
   public String checkout(Model model) {
       model.addAttribute("amount", 50 * 100); // in cents
       model.addAttribute("stripePublicKey", stripePublicKey);
       model.addAttribute("currency", ChargeRequest.Currency.USD);
       return "checkout";
   }

   @Controller
   public class ChargeController {

       @Autowired
       private StripeService paymentsService;

       @PostMapping("/charge")
       public String charge(ChargeRequest chargeRequest, Model model) throws StripeException {
           chargeRequest.setDescription("Example charge");
           chargeRequest.setCurrency(Currency.USD);
           Charge charge = paymentsService.charge(chargeRequest);
           model.addAttribute("id", charge.getId());
           model.addAttribute("status", charge.getStatus());
           model.addAttribute("chargeId", charge.getId());
           model.addAttribute("balance_transaction", charge.getBalanceTransaction());
           return "result";
       }

       @ExceptionHandler(StripeException.class)
       public String handleError(Model model, StripeException ex) {
           model.addAttribute("error", ex.getMessage());
           return "result";
       }
   }
}
