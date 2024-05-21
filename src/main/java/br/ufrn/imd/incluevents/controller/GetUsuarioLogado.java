package br.ufrn.imd.incluevents.controller;

import org.springframework.security.core.context.SecurityContextHolder;

import br.ufrn.imd.incluevents.model.Usuario;

public class GetUsuarioLogado {
  public static Usuario getUsuarioLogado() {
    try {
      Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      return usuario;
    } catch (ClassCastException e) {
      return null;
    }
  }
}
