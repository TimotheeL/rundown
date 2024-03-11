package com.github.rundown.parser;

import com.github.rundown.parser.Expression.Action;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Target;

public class SectionBuilder {
  private Action action;
  private Metadata metadata;
  private Target target;
  private Recovery recovery;

  public SectionBuilder action(Action action) {
    this.action = action;
    return this;
  }

  public SectionBuilder metadata(Metadata metadata) {
    this.metadata = metadata;
    return this;
  }

  public SectionBuilder target(Target target) {
    this.target = target;
    return this;
  }

  public SectionBuilder recovery(Recovery recovery) {
    this.recovery = recovery;
    return this;
  }

  public Section build() {
    return new Section(action, metadata, target, recovery);
  }
}
