import os
import re

jsx_path = r'C:\Users\divya\.gemini\antigravity\scratch\stratops360\GhostFibers.jsx'
with open(jsx_path, 'r', encoding='utf-8') as f:
    src = f.read()

# 1. Clean preamble: remove React imports
preamble = src[:src.find('const GhostFibers =')]
preamble = preamble.replace("'use client';", '')
preamble = preamble.replace("import { useEffect, useRef } from 'react';", '')
preamble = preamble.replace("import './GhostFibers.css';", '')

# 2. Extract settings and effect body
comp_str = src[src.find('const GhostFibers ='):]

# Find the start of the effect
eff_token = '  useEffect(() => {\n'
eff_idx = comp_str.find(eff_token)
settings_block = comp_str[comp_str.find('  const settings = {'):eff_idx]

settings_block = settings_block.replace('const settingsRef = useRef(settings);\n  settingsRef.current = settings;', 'const settingsRef = { current: settings };')
settings_block = settings_block.replace('const settingsRef = useRef(settings);\r\n  settingsRef.current = settings;', 'const settingsRef = { current: settings };')

# Inside the effect
eff_content = comp_str[eff_idx + len(eff_token):]
# The effect ends before the last return of the component
cleanup_str = '    return () => {\n'
cleanup_idx = eff_content.find(cleanup_str)

# From start of effect up to the cleanup function
main_eff = eff_content[:cleanup_idx]

# Cleanup body
cleanup_body_start = cleanup_idx + len(cleanup_str)
# Find the closing of the effect
end_eff_marker = '    };\n  }, []);'
end_eff_idx = eff_content.find(end_eff_marker)
cleanup_body = eff_content[cleanup_body_start:end_eff_idx]

# Replace wakeRef.current with wakeRef
main_eff_clean = main_eff.replace('wakeRef.current = () => {', 'wakeFn = () => {')
main_eff_clean = main_eff_clean.replace('const container = containerRef.current;\n    const canvas = canvasRef.current;', '// container and canvas already resolved above')
main_eff_clean = main_eff_clean.replace('const container = containerRef.current;\r\n    const canvas = canvasRef.current;', '// container and canvas already resolved above')
main_eff_clean = main_eff_clean.replace('containerRef.current', 'container')
main_eff_clean = main_eff_clean.replace('canvasRef.current', 'canvas')

cleanup_body_clean = cleanup_body.replace('wakeRef.current = null;', 'wakeFn = null;')

vanilla_code = f"""/**
 * GhostFibers - React Bits Component Adapted for Vanilla JavaScript
 * Layered ribbons of fine glowing fibers that twist, fold and drift through soft depth of field.
 * Source: https://reactbits.dev/backgrounds/ghost-fibers
 * Registry: https://reactbits.dev/r/GhostFibers-JS-CSS.json
 */

(function (root, factory) {{
  if (typeof define === 'function' && define.amd) {{
    define([], factory);
  }} else if (typeof module === 'object' && module.exports) {{
    module.exports = factory();
  }} else {{
    root.GhostFibers = factory();
    root.initGhostFibers = root.GhostFibers.init;
  }}
}})(typeof self !== 'undefined' ? self : this, function () {{
'use strict';

{preamble.strip()}

  function initGhostFibers(containerOrSelector, userOptions) {{
    var options = userOptions || {{}};
    var container = typeof containerOrSelector === 'string'
      ? document.querySelector(containerOrSelector)
      : containerOrSelector;

    if (!container) {{
      console.warn('initGhostFibers: Container element not found', containerOrSelector);
      return null;
    }}

    var canvas = container.querySelector('canvas.ghost-fibers-canvas');
    if (!canvas) {{
      canvas = document.createElement('canvas');
      canvas.className = 'ghost-fibers-canvas';
      canvas.setAttribute('aria-hidden', 'true');
      container.appendChild(canvas);
    }}

    var glowColor = options.glowColor !== undefined ? options.glowColor : '#8b5cf6';
    var lineColor = options.lineColor;
    var secondaryColor = options.secondaryColor;
    var gradient = options.gradient !== undefined ? options.gradient : 'depth';
    var speed = options.speed !== undefined ? options.speed : 0.5;
    var scale = options.scale !== undefined ? options.scale : 1;
    var rotation = options.rotation !== undefined ? options.rotation : 18;
    var rollSpeed = options.rollSpeed !== undefined ? options.rollSpeed : 0.15;
    var layers = options.layers !== undefined ? options.layers : 3;
    var waves = options.waves !== undefined ? options.waves : 1;
    var twist = options.twist !== undefined ? options.twist : 1.2;
    var threads = options.threads !== undefined ? options.threads : 72;
    var volume = options.volume !== undefined ? options.volume : 0.7;
    var spread = options.spread !== undefined ? options.spread : 1;
    var density = options.density !== undefined ? options.density : 1;
    var thickness = options.thickness !== undefined ? options.thickness : 1;
    var flow = options.flow !== undefined ? options.flow : 0.4;
    var fiberStyle = options.fiberStyle !== undefined ? options.fiberStyle : 'dots';
    var tips = options.tips !== undefined ? options.tips : 0;
    var foldGlow = options.foldGlow !== undefined ? options.foldGlow : 1;
    var perspective = options.perspective !== undefined ? options.perspective : 0.65;
    var depthOfField = options.depthOfField !== undefined ? options.depthOfField : 0.85;
    var focusDepth = options.focusDepth !== undefined ? options.focusDepth : 0;
    var particles = options.particles !== undefined ? options.particles : 0.5;
    var sparkle = options.sparkle !== undefined ? options.sparkle : 0.5;
    var glow = options.glow !== undefined ? options.glow : 0.8;
    var brightness = options.brightness !== undefined ? options.brightness : 1;
    var vignette = options.vignette !== undefined ? options.vignette : 0.3;
    var grain = options.grain !== undefined ? options.grain : 0.06;
    var mouseInteraction = options.mouseInteraction !== undefined ? options.mouseInteraction : true;
    var hoverEffect = options.hoverEffect !== undefined ? options.hoverEffect : 'focus';
    var mouseStrength = options.mouseStrength !== undefined ? options.mouseStrength : 1;
    var pulseOnClick = options.pulseOnClick !== undefined ? options.pulseOnClick : true;
    var intro = options.intro !== undefined ? options.intro : true;
    var lightMode = options.lightMode !== undefined ? options.lightMode : false;
    var paused = options.paused !== undefined ? options.paused : false;
    var dpr = options.dpr;

{settings_block}
    var wakeFn = null;

{main_eff_clean}

    function destroy() {{
{cleanup_body_clean}
    }}

    return {{
      destroy: destroy,
      wake: function() {{ if (wakeFn) wakeFn(); }},
      updateSettings: function(newOpts) {{
        if (!newOpts) return;
        Object.assign(options, newOpts);
        // Refresh specific settings if needed
        if (newOpts.glowColor !== undefined) settings.glowColor = String(newOpts.glowColor);
        if (newOpts.depthOfField !== undefined) settings.depthOfField = clamp(newOpts.depthOfField, 0, 2);
        if (newOpts.twist !== undefined) settings.twist = Math.max(0, newOpts.twist);
        if (wakeFn) wakeFn();
      }}
    }};
  }}

  return {{
    init: initGhostFibers
  }};
}});
"""

out_js = r'C:\Users\divya\.gemini\antigravity\scratch\stratops360\src\main\resources\static\js\ghost-fibers.js'
with open(out_js, 'w', encoding='utf-8') as f:
    f.write(vanilla_code)

print('ghost-fibers.js created successfully, length:', len(vanilla_code))
