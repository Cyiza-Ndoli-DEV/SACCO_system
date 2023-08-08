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
                    <a class="nav-link" href="{{url ('/admin/addmembers')}}">
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
                <!-- Button to trigger the modal -->
<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#uploadModal">Upload CSV FILE</button>

<!-- The Modal -->
<div class="modal fade" id="uploadModal" tabindex="-1" role="dialog" aria-labelledby="uploadModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form action="{{ route('upload') }}" method="POST" enctype="multipart/form-data">
                @csrf
                <div class="modal-header">
                    <h5 class="modal-title" id="uploadModalLabel">Upload File</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <input type="file" name="excel_file">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Upload</button>
                </div>
            </form>
        </div>
    </div>
</div>

        
            </div>
        </div>
</nav>
<script>
    document.getElementById('uploadButton').addEventListener('click', function() {
        var form = document.getElementById('uploadForm');
        if (form.style.display === 'none') {
            form.style.display = 'block';
        } else {
            form.style.display = 'none';
        }
    });
</script>
