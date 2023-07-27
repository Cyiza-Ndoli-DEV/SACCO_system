<nav class="navbar navbar-expand-lg d-flex justify-content-end " color-on-scroll="500">
        <div class="container-fluid">
            <a class="navbar-brand" href="#"> {{ $navName }} </a>
            <button href="" class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" aria-controls="navigation-index" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-bar burger-lines"></span>
                <span class="navbar-toggler-bar burger-lines"></span>
                <span class="navbar-toggler-bar burger-lines"></span>
            </button>
            <div class="collapse navbar-collapse d-flex justify-content-end" id="navigation">
                <ul class="nav navbar-nav mr-auto">
                    <li class="nav-item">
                        <a href="#" class="nav-link" data-toggle="dropdown">
                            
                            <span class="d-lg-none">{{ __('Dashboard') }}</span>
                        </a>
                    </li>
                    <li class="nav-item @if($activePage == 'dashboard') active @endif">
                    <a class="nav-link" href="{{route('dashboard')}}">
                        <span class="no-icon">{{ __("Dashboard") }}</span>
                    </a>
                </li>

                    <li class="nav-item @if($activePage == 'table') active @endif">
                    <a class="nav-link" href="{{route('page.index', 'table')}}">
                        <span class="no-icon">{{ __('Pending Requests') }}</span>
                    </a>
                </li>
                <li class="nav-item @if($activePage == 'table') active @endif">
                    <a class="nav-link" href="{{route('page.index', 'add_members')}}">
                        <span class="no-icon">{{ __('Add sacco members') }}</span>
                    </a>
                </li>
                <li class="nav-item @if($activePage == 'user') active @endif">
                    <a class="nav-link" href="{{route('profile.edit')}}">
                        <span class="no-icon">{{ __("Admin Profile") }}</span>
                    </a>
                </li>
                
                </li>
                <li class="nav-item @if($activePage == 'notifications') active @endif">
                    <a class="nav-link" href="{{route('page.index', 'notifications')}}">
                        <i class="nc-icon nc-bell-55"></i>
                        <span class="no-icon">{{ __("Notifications") }}</span>
                    </a>
                </li>
                    <li class="nav-item ml-auto">
                        <form id="logout-form" action="{{ route('logout') }}" method="POST">
                            @csrf
                            <a class="nav-link" href="{{ route('logout') }}" onclick="event.preventDefault(); document.getElementById('logout-form').submit();"> 
                            <span class="no-icon">{{ __('Log out') }}</span> 
                            </a>
                        </form>
                    </li>
                </ul>
            </div>
        </div>
</nav>