package ru.taxi.adminpanel.vaddin.views.login;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | Adminpanel")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login;

    public LoginView() {
        login = new LoginForm();
        addClassName("login-view");
        setSizeFull();
        login.addForgotPasswordListener(forgotPasswordEvent -> {
            Span message = new Span();
            Dialog dialog = new Dialog();
            dialog.add(
                    new Html("<div>To restore password please contact support service.<br><br>support.panel@taxi.ru<br>8-(800)-422-31-23</div>"));
            dialog.setCloseOnOutsideClick(true);
            dialog.addDialogCloseActionListener(e -> {
                message.setText("Dialogue closed");
                dialog.close();
            });
            dialog.open();
        });
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        login.setAction("login");
        add(new H1("Adminpanel"), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}