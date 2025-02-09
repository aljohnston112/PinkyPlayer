# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information

project = 'PinkyPlayer'
copyright = '2024, Alexander Johnston'
author = 'Alexander Johnston'
release = '0.3.4'

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = ["sphinx_needs", "sphinx.ext.autosectionlabel"]

templates_path = ['_templates']
exclude_patterns = []

latex_elements = {
  'extraclassoptions': 'openany,oneside',
      'preamble': r'''
          % Disable chapter titles
          \usepackage{titlesec}
          \titleformat{\chapter}[display]
          {\normalfont\Huge\bfseries}{}{0pt}{\Huge}
      ''',
}
