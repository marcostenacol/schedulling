export interface Dictionary {
  nav: {
    schedule: string;
    services: string;
    availability: string;
    profile: string;
    themeLight: string;
    themeDark: string;
    signOut: string;
  };
  common: {
    save: string;
    saveChanges: string;
    cancel: string;
    back: string;
    search: string;
    add: string;
    loading: string;
    dateLabel: string;
    startLabel: string;
    endLabel: string;
    serviceLabel: string;
    descriptionLabel: string;
    bioLabel: string;
    dateFnsLocale: string;
  };
  auth: {
    loginTitle: string;
    registerTitle: string;
    emailLabel: string;
    passwordLabel: string;
    confirmPasswordLabel: string;
    fullNameLabel: string;
    accountTypeLabel: string;
    roleClient: string;
    roleProvider: string;
    signInAction: string;
    registerAction: string;
    noAccountQuestion: string;
    registerHereLink: string;
    hasAccountQuestion: string;
    loginHereLink: string;
    passwordsDoNotMatch: string;
    accountCreated: string;
    loginError: string;
    registerError: string;
  };
  schedule: {
    pageTitle: string;
    pageSubtitle: string;
    loading: string;
    newSchedule: string;
    detailsTitle: string;
    clientLabel: string;
    timeLabel: string;
    notesLabel: string;
    notAvailable: string;
    confirm: string;
    markCompleted: string;
    cancelSchedule: string;
    statusPending: string;
    statusConfirmed: string;
    statusCancelled: string;
    statusCompleted: string;
    modeOwnService: string;
    modeSearchProvider: string;
    selectService: string;
    noServicesYet: string;
    providerCodeLabel: string;
    providerCodePlaceholder: string;
    providerNotFound: string;
    noActiveServices: string;
    walkInNotice: string;
    guestNameLabel: string;
    guestNamePlaceholder: string;
    scheduleNotesLabel: string;
    scheduleNotesPlaceholder: string;
    durationLabel: (minutes: number) => string;
    availableSlotsLabel: string;
    noSlotsAvailable: string;
    customTimeLabel: string;
    confirmSchedule: string;
    outsideHoursWarning: string;
    outsideHoursConfirm: string;
    slotsLoadError: string;
    createError: string;
  };
  calendar: {
    next: string;
    previous: string;
    today: string;
    month: string;
    week: string;
    day: string;
    agenda: string;
    date: string;
    time: string;
    event: string;
    allDay: string;
    noEventsInRange: string;
    showMore: (total: number) => string;
    defaultClient: string;
  };
  services: {
    pageTitle: string;
    pageSubtitle: string;
    loading: string;
    newService: string;
    emptyTitle: string;
    emptySubtitle: string;
    emptyAction: string;
    deleteConfirm: (name: string) => string;
    editTitle: string;
    createTitle: string;
    nameLabel: string;
    namePlaceholder: string;
    descriptionPlaceholder: string;
    priceLabel: string;
    durationLabel: string;
    createAction: string;
    saveError: string;
    noDescription: string;
    editAction: string;
    deleteTitle: string;
    durationSuffix: string;
  };
  availability: {
    pageTitle: string;
    pageSubtitle: string;
    loading: string;
    newSlotTitle: string;
    configuringDay: string;
    deleteConfirm: string;
    slotsConfigured: (count: number) => string;
    closed: string;
    removeSlot: string;
    addSlot: string;
    specificTitle: string;
    specificSubtitle: string;
    noSpecificSlots: string;
    days: string[];
  };
  profile: {
    pageTitle: string;
    loading: string;
    loadError: string;
    editTitle: string;
    editAction: string;
    fullNameLabel: string;
    avatarSelected: string;
    avatarHint: string;
    saveError: string;
    typeProvider: string;
    typeClient: string;
    typeAdmin: string;
    providerCodeTitle: string;
    aboutMeTitle: string;
  };
  admin: {
    pageTitle: string;
    loading: string;
    columnEmail: string;
    columnRole: string;
    columnCreatedAt: string;
  };
}

export const pt: Dictionary = {
  nav: {
    schedule: 'Agendamentos',
    services: 'Serviços',
    availability: 'Disponibilidade',
    profile: 'Meu Perfil',
    themeLight: 'Tema claro',
    themeDark: 'Tema escuro',
    signOut: 'Sair',
  },
  common: {
    save: 'Salvar',
    saveChanges: 'Salvar Alterações',
    cancel: 'Cancelar',
    back: 'Voltar',
    search: 'Buscar',
    add: '+ Adicionar',
    loading: 'Carregando...',
    dateLabel: 'Data',
    startLabel: 'Início',
    endLabel: 'Término',
    serviceLabel: 'Serviço',
    descriptionLabel: 'Descrição',
    bioLabel: 'Bio',
    dateFnsLocale: 'pt-BR',
  },
  auth: {
    loginTitle: 'Bem-vindo de volta',
    registerTitle: 'Criar nova conta',
    emailLabel: 'E-mail',
    passwordLabel: 'Senha',
    confirmPasswordLabel: 'Confirmar senha',
    fullNameLabel: 'Nome completo',
    accountTypeLabel: 'Tipo de conta',
    roleClient: 'Cliente',
    roleProvider: 'Prestador de Serviço',
    signInAction: 'Entrar',
    registerAction: 'Registrar',
    noAccountQuestion: 'Não tem uma conta?',
    registerHereLink: 'Registre-se aqui',
    hasAccountQuestion: 'Já tem uma conta?',
    loginHereLink: 'Faça login aqui',
    passwordsDoNotMatch: 'As senhas não coincidem.',
    accountCreated: 'Conta criada com sucesso! Faça login.',
    loginError: 'Erro ao realizar login.',
    registerError: 'Erro ao registrar.',
  },
  schedule: {
    pageTitle: 'Minha Agenda',
    pageSubtitle: 'Visualize e gerencie seus próximos compromissos.',
    loading: 'Carregando agenda...',
    newSchedule: 'Novo Agendamento',
    detailsTitle: 'Detalhes do Agendamento',
    clientLabel: 'Cliente',
    timeLabel: 'Horário',
    notesLabel: 'Observações',
    notAvailable: 'N/A',
    confirm: 'Confirmar',
    markCompleted: 'Marcar como concluído',
    cancelSchedule: 'Cancelar agendamento',
    statusPending: 'Pendente',
    statusConfirmed: 'Confirmado',
    statusCancelled: 'Cancelado',
    statusCompleted: 'Concluído',
    modeOwnService: 'Meu serviço',
    modeSearchProvider: 'Agendar com prestador',
    selectService: 'Selecione um serviço',
    noServicesYet: 'Você ainda não cadastrou nenhum serviço.',
    providerCodeLabel: 'Código do prestador',
    providerCodePlaceholder: 'Ex: 48D23L',
    providerNotFound: 'Prestador não encontrado. Confira o código.',
    noActiveServices: 'Nenhum serviço ativo encontrado para este prestador.',
    walkInNotice: 'Agendamento walk-in — cliente sem conta no sistema.',
    guestNameLabel: 'Nome do cliente (opcional)',
    guestNamePlaceholder: 'Ex: João da Silva',
    scheduleNotesLabel: 'Descrição / observações (opcional)',
    scheduleNotesPlaceholder: 'Alguma observação sobre este atendimento...',
    durationLabel: (minutes) => `Duração em minutos (padrão do serviço: ${minutes}min)`,
    availableSlotsLabel: 'Horários disponíveis',
    noSlotsAvailable: 'Nenhum horário disponível nesta data.',
    customTimeLabel: 'Ou escolha outro horário (fora da disponibilidade cadastrada)',
    confirmSchedule: 'Confirmar agendamento',
    outsideHoursWarning:
      'está fora do horário de atendimento cadastrado pelo prestador para este dia. Tem certeza que quer agendar mesmo assim?',
    outsideHoursConfirm: 'Sim, agendar mesmo assim',
    slotsLoadError: 'Erro ao carregar horários disponíveis.',
    createError: 'Erro ao criar agendamento.',
  },
  calendar: {
    next: 'Próximo',
    previous: 'Anterior',
    today: 'Hoje',
    month: 'Mês',
    week: 'Semana',
    day: 'Dia',
    agenda: 'Agenda',
    date: 'Data',
    time: 'Horário',
    event: 'Evento',
    allDay: 'Dia inteiro',
    noEventsInRange: 'Nenhum agendamento neste período.',
    showMore: (total) => `+ ${total} mais`,
    defaultClient: 'Cliente',
  },
  services: {
    pageTitle: 'Meus Serviços',
    pageSubtitle: 'Gerencie os tipos de serviços que você oferece aos seus clientes.',
    loading: 'Carregando serviços...',
    newService: 'Novo Serviço',
    emptyTitle: 'Nenhum serviço cadastrado',
    emptySubtitle: 'Comece adicionando seu primeiro serviço para que os clientes possam agendar.',
    emptyAction: 'Adicionar Serviço',
    deleteConfirm: (name) => `Excluir o serviço "${name}"? Ele deixará de aparecer no catálogo.`,
    editTitle: 'Editar Serviço',
    createTitle: 'Novo Serviço',
    nameLabel: 'Nome do Serviço',
    namePlaceholder: 'Ex: Corte de Cabelo',
    descriptionPlaceholder: 'Descreva brevemente o que está incluído...',
    priceLabel: 'Preço (R$)',
    durationLabel: 'Duração (min)',
    createAction: 'Criar Serviço',
    saveError: 'Erro ao salvar serviço.',
    noDescription: 'Sem descrição.',
    editAction: 'Editar serviço',
    deleteTitle: 'Excluir serviço',
    durationSuffix: 'min',
  },
  availability: {
    pageTitle: 'Minha Agenda',
    pageSubtitle: 'Defina seus horários de atendimento recorrentes ou avulsos.',
    loading: 'Carregando agenda...',
    newSlotTitle: 'Novo Horário',
    configuringDay: 'Configurando para o dia:',
    deleteConfirm: 'Remover este horário?',
    slotsConfigured: (count) => `${count} horários configurados`,
    closed: 'Fechado',
    removeSlot: 'Remover horário',
    addSlot: '+ Adicionar',
    specificTitle: 'Horários avulsos',
    specificSubtitle: 'Disponibilidade extra, fora do padrão semanal, válida só numa data específica.',
    noSpecificSlots: 'Nenhum horário avulso cadastrado.',
    days: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
  },
  profile: {
    pageTitle: 'Configurações da Conta',
    loading: 'Carregando perfil...',
    loadError: 'Erro ao carregar dados do perfil.',
    editTitle: 'Editar Informações',
    editAction: 'Editar Perfil',
    fullNameLabel: 'Nome Completo',
    avatarSelected: 'Nova foto selecionada — salve para aplicar',
    avatarHint: 'Clique na foto para trocar o avatar',
    saveError: 'Erro ao atualizar perfil.',
    typeProvider: 'Prestador',
    typeClient: 'Cliente',
    typeAdmin: 'Administrador',
    providerCodeTitle: 'Código de prestador (compartilhe com clientes)',
    aboutMeTitle: 'Sobre mim',
  },
  admin: {
    pageTitle: 'Gestão de Usuários (Admin)',
    loading: 'Carregando...',
    columnEmail: 'E-mail',
    columnRole: 'Perfil',
    columnCreatedAt: 'Data de Criação',
  },
};
