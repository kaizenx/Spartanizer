package il.org.spartan.refactoring.preferences;

import org.eclipse.core.runtime.preferences.*;
import org.eclipse.jface.preference.*;

import il.org.spartan.refactoring.builder.*;

/**
 * This class is called by Eclipse when the plugin is first loaded and has no
 * default preference values. These are set by the values specified here.
 *
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2016/03/28
 */
public class PluginPreferencesDefaultValuesInitializer extends AbstractPreferenceInitializer {
  @Override public void initializeDefaultPreferences() {
    final IPreferenceStore ps = Plugin.getDefault().getPreferenceStore();
    //
    ps.setDefault(PluginPreferencesResources.PLUGIN_STARTUP_BEHAVIOR_ID, "remember");
    ps.setDefault(PluginPreferencesResources.NEW_PROJECTS_ENABLE_BY_DEFAULT_ID, true);
    ps.setDefault(PluginPreferencesResources.ENABLE_BINDING_RESOLUTION_ID, false);
    //
    for (final PluginPreferencesResources.WringGroup wr : PluginPreferencesResources.WringGroup.values())
      ps.setDefault(wr.getId(), "on");
  }
}
