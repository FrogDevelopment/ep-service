import "@vaadin/vaadin-lumo-styles/all-imports";

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
  <style>
    html {
    }

    [theme~="dark"] {
    }
  </style>
</custom-style>


<custom-style>
  <style>
    html {
      overflow:hidden;
    }
    vaadin-app-layout vaadin-tab a:hover {
      text-decoration: none;
    }
  </style>
</custom-style>

<dom-module id="app-layout-theme" theme-for="vaadin-app-layout">
  <template>
    <style>
      [part="navbar"] {
        align-items: center;
        justify-content: center;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
